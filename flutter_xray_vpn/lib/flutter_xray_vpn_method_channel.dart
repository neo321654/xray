import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_xray_vpn_platform_interface.dart';

class MethodChannelFlutterXrayVpn extends FlutterXrayVpnPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_xray_vpn');

  @override
  Future<void> startVpn(String configJson) async {
    await methodChannel.invokeMethod<void>('startVpn', {'configJson': configJson});
  }

  @override
  Future<void> stopVpn() async {
    await methodChannel.invokeMethod<void>('stopVpn');
  }
}