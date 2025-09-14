import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_xray_vpn_method_channel.dart';

abstract class FlutterXrayVpnPlatform extends PlatformInterface {
  /// Constructs a FlutterXrayVpnPlatform.
  FlutterXrayVpnPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterXrayVpnPlatform _instance = MethodChannelFlutterXrayVpn();

  /// The default instance of [FlutterXrayVpnPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterXrayVpn].
  static FlutterXrayVpnPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterXrayVpnPlatform] when
  /// they register themselves.
  static set instance(FlutterXrayVpnPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
