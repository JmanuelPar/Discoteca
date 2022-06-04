package com.diego.discoteca.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

/**
 * Save all available networks with an internet connection to a set (@validNetworks).
 * As long as the size of the set > 0, this LiveData emits true.
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 */
class ConnectivityStatus(context: Context) :
    LiveData<Boolean>() {

    companion object {
        var isInternetAvailable: Boolean = false
    }

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        val value = validNetworks.size > 0
        postValue(value)
        isInternetAvailable = value
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        /* Min SDK 23 -> activeNetwork
           When start the app with no Internet in phone. activeNetwork is null */
        val network = connectivityManager.activeNetwork
        if (network == null) {
            Timber.d("activeNetwork is null !")
            postValue(false)
            isInternetAvailable = false
        }
        // We must register networkCallback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        /* Called when a network is detected. If that network has internet, save it in the Set.
           Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network) */
        override fun onAvailable(network: Network) {
            Timber.d("onAvailable")
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            if (hasInternetCapability == true) {
                // check if this network actually has internet, PING
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }
        /* If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
           Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network) */
        override fun onLost(network: Network) {
            Timber.d("onLost")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

}

/**
 * Send a ping to googles primary DNS.
 * If successful, that means we have internet.
 */
object DoesNetworkHaveInternet {
    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try {
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Timber.d("PING Google Sucess !")
            true
        } catch (e: IOException) {
            Timber.e("PING Google Fail ! $e")
            false
        }
    }
}