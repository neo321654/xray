import 'package:flutter/material.dart';
import 'package:flutter_xray_vpn/flutter_xray_vpn.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _flutterXrayVpnPlugin = FlutterXrayVpn();

  // A sample Xray configuration. 
  // This config uses a 'freedom' outbound, which means it will just forward the traffic.
  // It's useful for testing the VPN setup without a real server.
  final String testConfig = '''
  {
    "inbounds": [
      {
        "port": 10809,
        "listen": "127.0.0.1",
        "protocol": "socks",
        "settings": {
          "auth": "noauth",
          "udp": true,
          "ip": "127.0.0.1"
        }
      }
    ],
    "outbounds": [
      {
        "protocol": "freedom",
        "settings": {}
      }
    ]
  }
  ''';

  Future<void> _startVpn() async {
    try {
      await _flutterXrayVpnPlugin.startVpn(testConfig);
    } catch (e) {
      // Handle error
      print('Failed to start VPN: $e');
    }
  }

  Future<void> _stopVpn() async {
    try {
      await _flutterXrayVpnPlugin.stopVpn();
    } catch (e) {
      // Handle error
      print('Failed to stop VPN: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Xray VPN Plugin Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: _startVpn,
                child: const Text('Start VPN'),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _stopVpn,
                child: const Text('Stop VPN'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}