package com.example.flutter_xray_vpn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class XrayVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var xrayProcess: Process? = null
    private val vpnJob = SupervisorJob()
    private val vpnScope = CoroutineScope(Dispatchers.IO + vpnJob)

    private lateinit var config: String

    companion object {
        const val ACTION_START = "com.example.flutter_xray_vpn.START"
        const val ACTION_STOP = "com.example.flutter_xray_vpn.STOP"
        const val EXTRA_CONFIG = "config_json"
        private const val NOTIFICATION_CHANNEL_ID = "XrayVpnServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START -> {
                config = intent.getStringExtra(EXTRA_CONFIG) ?: return START_NOT_STICKY
                startVpn()
                START_STICKY
            }
            ACTION_STOP -> {
                stopVpn()
                START_NOT_STICKY
            }
            else -> START_NOT_STICKY
        }
    }

    private fun startVpn() {
        createNotificationChannel()
        val notificationIntent = Intent(this, FlutterXrayVpnPlugin::class.java) // Replace with your main activity if needed
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Xray VPN")
            .setContentText("VPN is running...")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your own icon
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        vpnScope.launch {
            runXray()
        }
    }

    private fun stopVpn() {
        stopForeground(true)
        stopSelf()
    }

    private suspend fun runXray() = withContext(Dispatchers.IO) {
        try {
            val configFile = createConfigFile(config)
            val xrayPath = findXrayBinary()
            if (xrayPath == null) {
                // Handle error: binary not found
                return@withContext
            }

            val processBuilder = ProcessBuilder(xrayPath, "-c", configFile.absolutePath)
            xrayProcess = processBuilder.start()

            // TODO: Redirect process error stream for debugging

            establishVpn()

            xrayProcess?.waitFor()
        } catch (e: IOException) {
            // Handle exceptions
        }
    }

    private fun establishVpn() {
        if (vpnInterface != null) return

        val builder = Builder()
        builder.setSession("XrayVpn")
        builder.addAddress("10.0.0.1", 30)
        builder.addRoute("0.0.0.0", 0)
        builder.addDnsServer("8.8.8.8")

        vpnInterface = builder.establish() ?: throw IllegalStateException("Failed to establish VPN")
    }

    private fun createConfigFile(jsonConfig: String): File {
        val configFile = File(filesDir, "config.json")
        FileOutputStream(configFile).use {
            it.write(jsonConfig.toByteArray())
        }
        return configFile
    }

    private fun findXrayBinary(): String? {
        val nativeLibDir = applicationInfo.nativeLibraryDir
        val xrayFile = File(nativeLibDir, "libxray.so")
        if (xrayFile.exists()) {
            // Make it executable
            xrayFile.setExecutable(true)
            return xrayFile.absolutePath
        }
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Xray VPN Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vpnJob.cancel()
        xrayProcess?.destroy()
        try {
            vpnInterface?.close()
        } catch (e: IOException) {
            // Handle exception
        }
    }
}
