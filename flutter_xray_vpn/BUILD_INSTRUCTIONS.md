# How to Build Xray-core for Android

This document explains how to compile the Xray-core binary for different Android architectures and where to place it within the plugin.

## Prerequisites

- You need to have a working Go environment installed on your machine.
- You need to have the Xray-core source code. You can get it by cloning the official repository:
  ```sh
  git clone https://github.com/XTLS/Xray-core.git
  cd Xray-core
  ```

## Build Commands

You need to compile Xray-core as a shared library (`.so` file). The `XrayVpnService.kt` expects the binary to be named `libxray.so`.

Execute the following commands inside the Xray-core source directory.

### For arm64-v8a (most modern 64-bit devices)

```sh
CGO_ENABLED=1 GOOS=android GOARCH=arm64 go build -ldflags "-s -w" -o libxray.so ./main
```

### For armeabi-v7a (older 32-bit devices)

```sh
CGO_ENABLED=1 GOOS=android GOARCH=arm GOARM=7 go build -ldflags "-s -w" -o libxray.so ./main
```

## Placement

After building the `libxray.so` file for each architecture, you need to place it in the correct directory inside the Flutter plugin.

1.  Create the `jniLibs` directory inside `android/src/main/` if it doesn't exist.
2.  Inside `jniLibs`, create subdirectories for each architecture.
3.  Copy the compiled `libxray.so` files into their respective architecture folders.

The final structure should look like this:

```
flutter_xray_vpn/
└── android/
    └── src/
        └── main/
            └── jniLibs/
                ├── arm64-v8a/
                │   └── libxray.so
                └── armeabi-v7a/
                    └── libxray.so
```

Once the binaries are in place, the plugin will be able to find and execute them at runtime.
