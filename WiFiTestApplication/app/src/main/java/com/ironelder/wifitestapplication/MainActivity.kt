package com.ironelder.wifitestapplication

import android.app.Service
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent

import android.content.BroadcastReceiver
import android.os.IBinder
import android.app.NotificationManager

import android.app.NotificationChannel

import androidx.core.app.NotificationCompat

import android.app.PendingIntent
import android.os.Handler


class MainActivity : AppCompatActivity() {

    val wifiReceiver = WiFiReceiver()
    val wifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
        startService(Intent(this, MyService::class.java))
    }

    private fun test(){
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
//        registerReceiver(wifiReceiver, filter)
        val wifiState = wifiManager.isWifiEnabled //set isWifiEnabled 으로 변경 가능
        Log.d("ironelderTest", "wifiState = $wifiState")
        Log.d("ironelderTest", "info = ${wifiManager.connectionInfo.ssid}")

    }
    private fun test2() {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = String.format("\"%s\"", "test")
        wifiConfig.preSharedKey = String.format("\"%s\"", "testKey")
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)

        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()

        wifiManager.enableNetwork(netId, true)
    }
}

class MyService : Service() {
    private val NOTIF_ID = 1
    private val NOTIF_CHANNEL_ID = "AppNameBackgroundService"
    private lateinit var thread:ServiceThread
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        thread =
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        var broadcastReceiver: BroadcastReceiver? = WiFiReceiver()
        registerReceiver(broadcastReceiver, filter)
        startForeground()
        return super.onStartCommand(intent, flags, startId)

    }

//    override fun onCreate() {
//        // create IntentFilter
//        //add actions
////        val filter = IntentFilter()
////        filter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
////        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
////        registerReceiver(broadcastReceiver, filter)
//    }

    private fun startForeground() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        startForeground(
            NOTIF_ID, NotificationCompat.Builder(
                this,
                NOTIF_CHANNEL_ID
            ) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("test")
                .setContentText("Background service is running")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    inner class myServiceHandler : Handler() {

    }
}

class ServiceThread(handler:Handler) : Thread() {
    private var handler:Handler = handler
    private var isRun = true

    fun stopForever() {
        synchronized(this) {
            isRun = false
        }
    }

    override fun run() {
        while (isRun) {
            handler.sendEmptyMessage(0)
            try {
                Thread.sleep(3000)
            } catch (e:Exception){

            }
        }
    }
}
