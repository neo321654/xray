
import 'flutter_xray_vpn_platform_interface.dart';

class FlutterXrayVpn {
  Future<String?> getPlatformVersion() {
    return FlutterXrayVpnPlatform.instance.getPlatformVersion();
  }
}
