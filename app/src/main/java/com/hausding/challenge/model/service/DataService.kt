package com.hausding.challenge.model.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import com.hausding.challenge.R
import com.hausding.challenge.model.api.CoinDataManager
import com.hausding.challenge.model.api.coingecko.CoinGeckoHandler
import com.hausding.challenge.view.MainActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * Service class for data retrieval
 * @property wakeLock Wakelock so service doesnt get killed in background
 * @property state Current service state
 * @property dataJob Job handling data retrieval
 */
class DataService : Service() {
    companion object {
        private const val WAKELOCK_TAG = "DataService::lock"
        private const val TAG = "[DATASERVICE]"
        private const val SERVICE_NOTIFICATION_CHANNEL = "DataServiceChannel"
        private const val SERVICE_NOTIFICATION_CHANNEL_NAME = "Data Service Notification Channel"
        private const val SERVICE_NOTIFICATION_CHANNEL_DESC = "Data Service Channel"
        private const val SERVICE_NOTIFICATION_TITLE = "Data Service"
        private const val SERVICE_NOTIFICATION_TEXT = "Retrieving data..."
        private const val SERVICE_ID = 1000
        private const val API_RETRIEVAL_RATE_S = 10L
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var state = DataServiceState.STOPPED
    private var dataJob: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ServiceAction.START.name -> {
                    startService()
                }
                ServiceAction.STOP.name -> {
                    stopService()
                }
                ServiceAction.CHANGE_CURRENCY.name -> {
                    dataJob?.cancel()
                    checkDataSource()
                }
                else -> {
                    Log.d(TAG, "Intents without actions should not happen")
                }
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SERVICE_ID, buildNotification())
    }

    /**
     * Function for service start handling, creates wakelock, initializes CoinDataManager with CoinGecko data source and cache
     */
    private fun startService() {
        if (state != DataServiceState.STOPPED) {
            return
        }

        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG).apply {
                    acquire()
                }
            }
        CoinDataManager.loadCache(
            this,
            loadCallback = {
                CoinDataManager.installDataSource(CoinGeckoHandler())
                checkDataSource()
            }
        )
    }

    private fun checkDataSource() {
        state = DataServiceState.LOAD_DATA
        dataJob = GlobalScope.launch(Dispatchers.IO) {
            var ready = false
            while (state != DataServiceState.STOPPED && !ready) {
                CoinDataManager.checkAvailable(
                    onAvailable = {
                        ready = true
                        startRequestHandler()
                    },
                    onError = {
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@DataService, R.string.toast_error_source_unavailable, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                delay(TimeUnit.SECONDS.toMillis(API_RETRIEVAL_RATE_S))
            }
        }
    }
    /**
     * Function for starting data retrieval
     */
    private fun startRequestHandler() {
        dataJob = GlobalScope.launch(Dispatchers.IO) {
            while (state != DataServiceState.STOPPED) {
                if (state == DataServiceState.LOAD_DATA) {
                    launch(Dispatchers.IO) {
                        CoinDataManager.initialLoad()
                    }
                    launch(Dispatchers.Main) {
                        if (state != DataServiceState.STOPPED) {
                            state = DataServiceState.UPDATE_DATA
                        }
                    }
                }
                launch(Dispatchers.IO) {
                    CoinDataManager.retrieveCurrentRate(this@DataService)
                }
                delay(TimeUnit.SECONDS.toMillis(API_RETRIEVAL_RATE_S))
            }
        }
    }

    /**
     * Function for stopping service and cancelling data retrieval
     */
    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
            state = DataServiceState.STOPPED
            dataJob?.cancel()
        } catch (ex: Exception) {
            Log.d(TAG,"Error stopping service $ex")
        }
    }

    /**
     * Function for building required notification for foreground service
     * @return Notification Object
     */
    private fun buildNotification(): Notification {
        val channel = NotificationChannel(
            SERVICE_NOTIFICATION_CHANNEL,
            SERVICE_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = SERVICE_NOTIFICATION_CHANNEL_DESC
            it
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)


        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        return Notification.Builder(this, SERVICE_NOTIFICATION_CHANNEL)
            .setContentTitle(SERVICE_NOTIFICATION_TITLE)
            .setContentText(SERVICE_NOTIFICATION_TEXT)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}