package org.panta.misskey_for_android_v2.repository

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.panta.misskey_for_android_v2.entity.User
import org.panta.misskey_for_android_v2.network.HttpsConnection
import org.panta.misskey_for_android_v2.network.StreamConverter
import java.net.URL

class UserRepository(private val domain: String, private val authKey: String){

    //TODO UserTimelineは機能が少ないので統合する
    private val httpsConnection = HttpsConnection()
    fun getUserInfo(userPrimaryId: String, callBack: (User)->Unit){
        GlobalScope.launch{
            try{
                val json = "{\"userId\":\"$userPrimaryId\"}"
                val responseStream = httpsConnection.post(URL("$domain/api/users/show"), json)
                val responseJson = StreamConverter().getString(responseStream)
                Log.d("UserRepository", responseJson)
                val obj = jacksonObjectMapper().readValue<User>(responseJson)
                callBack(obj)
            }catch(e: Exception){
                Log.w("UserRepository", "getUserInfoでエラー発生", e)
            }
        }
    }
}