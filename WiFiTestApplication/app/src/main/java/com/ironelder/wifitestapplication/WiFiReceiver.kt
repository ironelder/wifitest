package com.ironelder.wifitestapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log

class WiFiReceiver : BroadcastReceiver() {

    var wifiConnection = false

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("ironelderTest", "info all action = ${intent?.action}")
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                cm?.requestNetwork(wifiNetworkRequest, object :
                    ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
                        val ssid = wifiInfo?.ssid
                        val bssid = wifiInfo?.bssid
                        Log.d("ironelderTest", "networkCapabilities = $wifiInfo ssid = $ssid , bssid = $bssid")
                    }

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        val activeStateNetwork = cm.activeNetwork
                        val actNetwork = cm.getNetworkCapabilities(activeStateNetwork)

                        val info = actNetwork?.transportInfo as? WifiInfo
                        val ssid = info?.ssid
                        val bssid = info?.bssid
                        Log.d("ironelderTest", "WIFIinfo = $info ssid = $ssid , bssid = $bssid")

                        if (actNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true){
                            Log.d("ironelderTest", "onAvailable WIFI = $network")
                        } else {
                            Log.d("ironelderTest", "onAvailable DATA = $network")
                        }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        Log.d("ironelderTest", "onLost = $network")
                    }
                })
            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && false -> {
//                cm?.registerNetworkCallback(wifiNetworkRequest, object :
//                    ConnectivityManager.NetworkCallback() {
//
//                    override fun onCapabilitiesChanged(
//                        network: Network,
//                        networkCapabilities: NetworkCapabilities
//                    ) {
//                        super.onCapabilitiesChanged(network, networkCapabilities)
//                        val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
//                        val ssid = wifiInfo?.ssid
//                        val bssid = wifiInfo?.bssid
//                        Log.d("ironelderTest", "networkCapabilities = $wifiInfo ssid = $ssid , bssid = $bssid")
//                    }
//
//                    override fun onAvailable(network: Network) {
//                        super.onAvailable(network)
//                        val activeStateNetwork = cm.activeNetwork
//                        val actNetwork = cm.getNetworkCapabilities(activeStateNetwork)
//
//                        val info = actNetwork?.transportInfo as? WifiInfo
//                        val ssid = info?.ssid
//                        val bssid = info?.bssid
//                        Log.d("ironelderTest", "WIFIinfo = $info ssid = $ssid , bssid = $bssid")
//
//                        if (actNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true){
//                            Log.d("ironelderTest", "onAvailable WIFI = $network")
//                        } else {
//                            Log.d("ironelderTest", "onAvailable DATA = $network")
//                        }
//                    }
//
//                    override fun onLost(network: Network) {
//                        super.onLost(network)
//                        Log.d("ironelderTest", "onLost = $network")
//                    }
//                })
//            }
            else -> {
                if (intent?.action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    val networkInfo = cm?.activeNetworkInfo
                    if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                        val wifiConnectionInfo = wifiManager?.connectionInfo
                        val ssid = wifiConnectionInfo?.ssid
                        Log.d("ironelderTest", "info = $ssid")
                    }
                } else if (intent?.action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION, true)) {
                    val wifiState = intent?.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )
                    if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        wifiConnection = false
                    }
                    val networkInfo = cm?.activeNetworkInfo
                    if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                        val wifiConnectionInfo = wifiManager?.connectionInfo
                        val ssid = wifiConnectionInfo?.ssid
                        Log.d("ironelderTest", "info action = ${intent?.action}")
                        Log.d("ironelderTest", "info ssid = $ssid")
                    }
                }
            }
        }

    }
}