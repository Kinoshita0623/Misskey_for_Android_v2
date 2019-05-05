package org.panta.misskey_for_android_v2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.panta.misskey_for_android_v2.entity.CreateNoteProperty
import org.panta.misskey_for_android_v2.entity.FileProperty
import org.panta.misskey_for_android_v2.network.OkHttpConnection
import org.panta.misskey_for_android_v2.repository.SecretRepository
import org.panta.misskey_for_android_v2.storage.SharedPreferenceOperator
import org.panta.misskey_for_android_v2.view_presenter.user_auth.AuthActivity
import java.io.File
import java.net.URL

class NotePostService : Service() {

    companion object{
        const val FILE_NAME_ARRAY_CODE = "NotePostServiceFileNameArrayCode"
        const val NOTE_BUILDER_CODE = "NotePostServiceNoteBuilderPropertyCode"
    }

    private lateinit var i: String
    private lateinit var domain: String

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val sharedPref = SecretRepository(SharedPreferenceOperator(this))
        val info = sharedPref.getConnectionInfo()
        if(info == null){
            startActivity(Intent(applicationContext, AuthActivity::class.java))
            stopSelf()
            return
        }else{
            i = info.i
            domain = info.domain
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val noteBuilder = intent?.getSerializableExtra(NOTE_BUILDER_CODE) as CreateNoteProperty.Builder

        val fileNameArray = intent.getStringArrayExtra(FILE_NAME_ARRAY_CODE)
        GlobalScope.launch{
            val file = fileNameArray?.map { File(it) }?.map{
                jacksonObjectMapper().readValue<FileProperty>(uploadFile(it)!!).id!!
            }

            noteBuilder.fileIds = file
            val noteProperty = noteBuilder.create()
            val result = OkHttpConnection().postString(URL("$domain/api/notes/create"), jacksonObjectMapper().writeValueAsString(noteProperty))
            if(result == null){
                Toast.makeText(applicationContext, "投稿に失敗しました", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "投稿に成功しました", Toast.LENGTH_LONG).show()
            }
            stopSelf()

        }

        return START_NOT_STICKY

    }

    private suspend fun uploadFile(file: File): String?{
        val connection = OkHttpConnection()
        return connection.postFile(URL("$domain/api/drive/files/create"), i = i, file = file, force = true)

    }
}
