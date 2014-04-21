# WizTurn Beacon SDK for Android

## Introduction

wizTurn Beacon is a device transmitting position signals by applying specification of [Bluetooth Low Energy](http://www.bluetooth.com/pages/bluetooth-smart.aspx).  iOS, and Android smart phones supporting Bluetooth Low Energy can even affirm an approximate position indoors by receiving information about the exact location and situation. 
wizTurn Beacon SDK allows easy creation of a variety of apps on iOS, Android platforms by applying a wizTurn Beacon product. More detailed functions than provided by SDK can be checked through [iOS API Document](http://wizturn.github.io/iOS-SDK/), [Android API Document](http://wizturn.github.io/Android-SDK/).

## Overview

wizTurn Beacon SDK recognizes signals of a beacon , and the specifications of the signal is passed onto the connected app..SDK scans periodically to catch signals of a beacon while an applied app is running in the background of a smart phone. Upon meeting signals of a beacon during periodic scans, signal information such as UUID, Major, Minor are transmitted to the app. Developers of applied apps can utilize such information to develop desired functions.
Signal specification of a beacon is based on the specification of Apple's iBeacon, and the detailed contents shall be referred to [CLBeacon Class Reference](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLBeacon_class/Reference/Reference.html).

## Android SDK
Android SDK operates in a version higher than Android 4.3 while supporting Bluetooth Low Energy. Android consists of 6 packages.
- com.wizturn.sdk package is a package providing the overall function of SDK. It is provided with a list of beacons, received signal strength indication (RSSI) from a beacon, andproximity values.
   A proximity has values of  immediate, near, far, meaning less than 0.5m, within 0.5~3m, more than 3m, respectively.
- com.wizturn.sdk.connect package  provides a function of connecting with individual beacons, and can read battery condition, TX Power, etc. information from a beacon.

### Android SDK Install
1. Download WizturnSDK  from [wizTurn Android SDK](https://github.com/wizTurn/Android-SDK).
2. Copy [wizturnbeaconsdk.jar](https://github.com/wizTurn/Android-SDK/blob/master/wizTurnSDK/wizturnbeaconsdk.jar) to your `libs` directory.
3. A project can be started right now.  For more detailed contents on a project configuration and applications, please refer to [Illustration] (https://github.com/wizTurn/Android-SDK/tree/master/Examples/) 

### Changelog 
See the [CHANGELOG](https://github.com/wizTurn/android-SDK/blob/master/CHANGELOG.md).


COPYRIGHT(C) 2014 SK TELECOM. ALL RIGHTS RESERVED.
