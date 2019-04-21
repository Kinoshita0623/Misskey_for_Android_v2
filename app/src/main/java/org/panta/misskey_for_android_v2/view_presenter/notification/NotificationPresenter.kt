package org.panta.misskey_for_android_v2.view_presenter.notification

import org.panta.misskey_for_android_v2.constant.ApplicationConstant
import org.panta.misskey_for_android_v2.entity.NotificationProperty
import org.panta.misskey_for_android_v2.repository.Notification

class NotificationPresenter(private val mView: NotificationContract.View) : NotificationContract.Presenter{

    private var latestNotification: NotificationProperty? = null
    private var oldestNotification: NotificationProperty? = null

    private val mNotification = Notification(domain = ApplicationConstant.domain, authKey = ApplicationConstant.authKey)

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOldNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initNotification() {
        mNotification.getNotification {
            if(it.isEmpty()){
                mView.stopRefreshing()
                return@getNotification
            }

            mView.showInitNotification(it)

            latestNotification = it.first()
            oldestNotification = it.last()

        }
    }
}