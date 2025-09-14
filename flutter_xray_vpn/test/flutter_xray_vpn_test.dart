import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_xray_vpn/flutter_xray_vpn.dart';
import 'package:flutter_xray_vpn/flutter_xray_vpn_platform_interface.dart';
import 'package:flutter_xray_vpn/flutter_xray_vpn_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterXrayVpnPlatform
    with MockPlatformInterfaceMixin
    implements FlutterXrayVpnPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterXrayVpnPlatform initialPlatform = FlutterXrayVpnPlatform.instance;

  test('$MethodChannelFlutterXrayVpn is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterXrayVpn>());
  });

  test('getPlatformVersion', () async {
    FlutterXrayVpn flutterXrayVpnPlugin = FlutterXrayVpn();
    MockFlutterXrayVpnPlatform fakePlatform = MockFlutterXrayVpnPlatform();
    FlutterXrayVpnPlatform.instance = fakePlatform;

    expect(await flutterXrayVpnPlugin.getPlatformVersion(), '42');
  });
}
