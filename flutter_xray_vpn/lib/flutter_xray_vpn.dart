import 'flutter_xray_vpn_platform_interface.dart';

class FlutterXrayVpn {
  Future<void> startVpn(String configJson) {
    return FlutterXrayVpnPlatform.instance.startVpn(configJson);
  }

  Future<void> stopVpn() {
    return FlutterXrayVpnPlatform.instance.stopVpn();
  }
}