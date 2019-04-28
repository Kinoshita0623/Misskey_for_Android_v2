package org.panta.misskey_for_android_v2.view_presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.panta.misskey_for_android_v2.R
import org.panta.misskey_for_android_v2.constant.TimelineTypeEnum
import org.panta.misskey_for_android_v2.entity.DomainAuthKeyPair
import org.panta.misskey_for_android_v2.entity.User
import org.panta.misskey_for_android_v2.interfaces.ISharedPreferenceOperator
import org.panta.misskey_for_android_v2.interfaces.MainContract
import org.panta.misskey_for_android_v2.storage.SharedPreferenceOperator
import org.panta.misskey_for_android_v2.view_presenter.mixed_timeline.MixedTimelineFragment
import org.panta.misskey_for_android_v2.view_presenter.note_editor.EditNoteActivity
import org.panta.misskey_for_android_v2.view_presenter.notification.NotificationFragment
import org.panta.misskey_for_android_v2.view_presenter.timeline.TimelineFragment
import org.panta.misskey_for_android_v2.view_presenter.user_auth.AuthActivity

private const val FRAGMENT_HOME = "FRAGMENT_HOME"
private const val FRAGMENT_OTHER = "FRAGMENT_OTHER"
const val DOMAIN_AUTH_KEY_TAG = "MainActivityUserDomainAndAuthKey"
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    override lateinit var mPresenter: MainContract.Presenter
    //private var i: String? = null
    //private var domain: String? = null
    private lateinit var sharedOperator: ISharedPreferenceOperator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        fab.setOnClickListener {
            val intent = Intent(applicationContext, EditNoteActivity::class.java)
            startActivity(intent)
        }


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*~~basic init*/
        sharedOperator = SharedPreferenceOperator(getSharedPreferences("privateUserData", Context.MODE_PRIVATE))

        mPresenter = MainPresenter(this, sharedOperator)

        val authKey = intent.getSerializableExtra(DOMAIN_AUTH_KEY_TAG) as DomainAuthKeyPair?
        if(authKey != null){
            mPresenter.saveConnectInfo(authKey)
        }

       mPresenter.getPersonalProfile()

        mPresenter.initDisplay()

        mPresenter.getPersonalProfile()

    }


    override fun initDisplay(connectionInfo: DomainAuthKeyPair) {
        val sf = supportFragmentManager
        val ft = sf.beginTransaction()
        ft.replace(R.id.main_container, TimelineFragment.getInstance(info = connectionInfo  , type = TimelineTypeEnum.HOME))
        ft.commit()

        bottom_navigation.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when(it.itemId){
                R.id.home_timeline ->{
                    setFragment(TimelineFragment.getInstance(connectionInfo, type = TimelineTypeEnum.HOME), FRAGMENT_HOME)
                    true
                }
                R.id.mix_timeline ->{
                    setFragment(MixedTimelineFragment.getInstance(connectionInfo), FRAGMENT_OTHER)
                    true
                }
                R.id.notification_item ->{
                    setFragment(NotificationFragment.getInstance(connectionInfo), FRAGMENT_OTHER)
                    true
                }
                R.id.message_item ->{
                    false
                }
                else -> false

            }
        }
    }

    override fun showAuthActivity() {
        runOnUiThread{
            val intent = Intent(applicationContext, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    override fun showPersonalProfile(user: User) {
        runOnUiThread {
            if(user.avatarUrl != null){
                Picasso
                    .get()
                    .load(user.avatarUrl)
                    .into(my_account_icon)
            }

            my_name.text = user.name?: user.userName

            val userName = if(user.host != null){
                "@${user.userName}@${user.host}"
            }else{
                "@${user.userName}"
            }
            my_user_name.text = userName

            follower_count.text = user.followersCount.toString()
            following_count.text= user.followingCount.toString()
        }
    }


    private fun setFragment(fragment: Fragment, fragmentName: String){
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.main_container, fragment)

        val count = fm.backStackEntryCount

        if(fragmentName == FRAGMENT_OTHER){
            ft.addToBackStack(fragmentName)
        }
        ft.commit()

        fm.addOnBackStackChangedListener( object : android.support.v4.app.FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                if(fm.backStackEntryCount <= count){
                    fm.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE)
                    fm.removeOnBackStackChangedListener(this)
                    bottom_navigation.menu.getItem(0).isChecked = true
                }
            }
        })
    }



    override fun onBackPressed() {

        //NavigationDrawerが開いているときに戻るボタンを押したときの動作
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }
        val selectedItemId = bottom_navigation?.selectedItemId
        if(R.id.home_timeline != selectedItemId){
            bottom_navigation.selectedItemId = R.id.home_timeline
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
