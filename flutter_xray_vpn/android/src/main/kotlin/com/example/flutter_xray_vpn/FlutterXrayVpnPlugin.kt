package com.example.flutter_xray_vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener

class FlutterXrayVpnPlugin :
    FlutterPlugin,
    MethodCallHandler,
    ActivityAware,
    ActivityResultListener {

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var pendingResult: Result? = null
    private var config: String? = null

    companion object {
        private const val VPN_REQUEST_CODE = 101
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_xray_vpn")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "startVpn" -> {
                val config = call.argument<String>("configJson")
                if (config == null) {
                    result.error("INVALID_ARGUMENT", "configJson is null", null)
                    return
                }
                this.config = config
                startVpn(result)
            }
            "stopVpn" -> {
                stopVpn()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    private fun startVpn(result: Result) {
        val context = activity ?: return result.error("NO_ACTIVITY", "Plugin is not attached to an activity.", null)

        val vpnIntent = VpnService.prepare(context)
        if (vpnIntent != null) {
            pendingResult = result
            activity?.startActivityForResult(vpnIntent, VPN_REQUEST_CODE)
        } else {
            // Permission already granted
            onActivityResult(VPN_REQUEST_CODE, Activity.RESULT_OK, null)
            result.success(null) // Immediately return success as the process has started
        }
    }

    private fun stopVpn() {
        activity?.let {
            val intent = Intent(it, XrayVpnService::class.java).setAction(XrayVpnService.ACTION_STOP)
            it.startService(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.let {
                    val intent = Intent(it, XrayVpnService::class.java).apply {
                        action = XrayVpnService.ACTION_START
                        putExtra(XrayVpnService.EXTRA_CONFIG, config)
                    }
                    it.startService(intent)
                }
                pendingResult?.success(null)
            } else {
                pendingResult?.error("PERMISSION_DENIED", "User did not grant VPN permission", null)
            }
            pendingResult = null
            return true
        }
        return false
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}