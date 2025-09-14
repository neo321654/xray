import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_xray_vpn_method_channel.dart';

abstract class FlutterXrayVpnPlatform extends PlatformInterface {
  FlutterXrayVpnPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterXrayVpnPlatform _instance = MethodChannelFlutterXrayVpn();

  static FlutterXrayVpnPlatform get instance => _instance;

  static set instance(FlutterXrayVpnPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> startVpn(String configJson) {
    throw UnimplementedError('startVpn() has not been implemented.');
  }

  Future<void> stopVpn() {
    throw UnimplementedError('stopVpn() has not been implemented.');
  }
}