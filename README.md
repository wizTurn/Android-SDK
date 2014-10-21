# WizTurn Beacon SDK for Android
This SDK provides a simple, easy and useful toolkit to develop BLE related mobile applications.
It makes developers to create an Android application communicating with peripherals(e.g. beacons) without delving into Android BLE or Bluetooth API in details. Only Android 4.3+ supported.

## Features
* pebBLE, nimBLE property configuration
* Scanning, connecting BLE devices

Android 4.3+ support

## [Changelog](https://github.com/wizTurn/Android-SDK/blob/master/CHANGELOG.md)

## Quick Setup

### Where's the old sdk???
You can find the old version of WIZTURN SDK in the follow directory.
** Old WIZTURN SDK ***(https://github.com/wizTurn/Android-SDK/tree/master/wizturn-sdk1.1-old)

### 1. Include library
* [Downlaod](https://github.com/wizTurn/Android-SDK/tree/master/wizTurnSDK/wizturnbeaconsdk-2.0.0.jar)
* Put the jar file in the **libs** sub directory of your Android project

### 2. Android Manifest
    <manifest>
        ...
        <!-- Include following permission for allowing applications to connect to paired bluetooth devices -->
        <uses-permission android:name="android.permission.BLUETOOTH"/>
        <!-- Include following permission for allowing applications to discover and pair bluetooth devices -->
        <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
        ...
    </manifest>

## Usage
All the classes you will need is as follows.
```
PeripheralListAdapter, CentralManager, PeripheralScanListener,  OnConnectListener, 
Peripheral, PeripheralAccessListener, PeripheralChangeEvent, BluetoothGattWriter
```
** Notice : Every method of CentralManager, BluetoothGattWriter must be called on Main UI Thread. Otherwise unexpected behavior takes place.**
### Scanning
Suppose you placed some beacons at specific and meaninful places to provide your own service. 
Your Android smartphone will scan every peripheral(beacon) sending advertisement packet around you.
Just play with it just found.
#### 1. Start scanning
A peripheral(e.g. beacon) which sends advertisement packet to the central(e.g. your Android phone) will be 
returned on PeripheralScanListener.onPeripheralScan(Central central, Peripheral peripheral) callback
method. And you can play with the returned peripheral accordingly.
```
CentralManager centralManager = CentralManager.getInstance(getApplicationContext());
centralManager.setPeripheralScanListener(new PeripheralScanListener() {
    @Override
	public void onPeripheralScan(Central central, final Peripheral peripheral) {
		// TODO do whatever you want with Peripheral(namely beaocn)			
	}			
});
// if bluetooth is disabled, then turn on before going further
if(!centralManager.isBluetoothEnabled()) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	startActivityForResult(enableBtIntent, 100);
}
...
centralManager.startScanning();
```
#### 2. Stop Scanning
To stop scanning, only call CentralManager.stopScanning() as follows.
```
CentralManager centralManager = CentralManager.getInstance(getApplicationContext());
centralManager.stopScanning();
```
### Connection
Through connecting to a peripheral(e.g. beacon), you can change beacon properties, 
UUID, major id, minor id, txPower, time interval and so on. And **keep in mind that 
only one single peripheral can be connected at a time. Multiple connection will fail.**

#### 1. Connecting
Having scanned some peripherals, connection to the found peripheral is now available.
```
CentralManager centralManager = CentralManager.getInstance(getApplicationContext());
// Use either connect() or connectWithDelay(). connectWithDelay() is recommended for safely connection
// centralManager.connect(peripheral, onConnectListener);
centralManager.connectWithDelay(peripheral, onConnectListener);
...
...

private OnConnectListener onConnectListener = new OnConnectListener() {
    @Override
	public void onDisconnected(final Peripheral peripheral) {
        // TODO the peripheral is now disconnected, do whatever you want
	}
		
	@Override
	public void onError(final Peripheral peripheral) {
        // TODO connection to the peripheral has errors, do whatever you want
	}
		
	@Override
	public void onConnected(final Peripheral peripheral, 
        final boolean needAuthentication) {
		// TODO the peripheral is now connected, do whatever you want
	}
};
```
#### 2. Disconnecting
Disconnectin to a peripheral is very easy like below. **Also keep in mind that 
disconnection must be perforemd after connecting to one of peripherals.** Otherwise 
your Android bluetooth behaves unexpectedly.
```
CentralManager centralManager = CentralManager.getInstance(getApplicationContext());
if(centralManager.isConnected()) {
    // Use either disconnect() or disconnectWithDelay(). disconnectWithDelay() is recommended for safely disconnection.
    // centralManager.disconnect();
    centralManager.disconnectWithDelay();
}
```

### Changing properties of peripherals(beacons)
Changing peripheral properties(txPower, time interval, UUID, major id, minor id and so on) is little bit tricky than others.
Suppose a peripheral is already connected and you want to change properties of it.
Also **Keep in mind that you only change a single property at a time. After changing is completed, change another property.**
```
CentralManager centralManager = CentralManager.getInstance(getApplicationContext());
final BluetoothGattWriter writer = centralManager.getBluetoothGattWriter();
writer.setPeripheralAccessListener(accessListener);
// Use either authenticate() or authenticateWithDelay(). authenticateWithDelay() is recommended for safely authencating.
// writer.authenticate("000000");
writer.authenticateWithDelay("000000"); // authenticates to the peripheral by password '000000'
...
...
private PeripheralAccessListener accessListener = new PeripheralAccessListener() {
    @Override
    public void onChangingCompleted(final Peripheral peripheral, 
        final PeripheralChangeEvent event) {
        // TODO  one of peripheral properties successfuly completed. Do whatever you want.
        
        final int changeEvent = event.getEvent();
    	switch(changeEvent) {
		    case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
			    break;
			case PeripheralChangeEvent.CHANGE_MAJOR:
				break;
			case PeripheralChangeEvent.CHANGE_MINOR:
				break;
			case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
				break;
			case PeripheralChangeEvent.CHANGE_TX_POWER:
				break;
			case PeripheralChangeEvent.CHANGE_PASSWORD:
				break;
			case PeripheralChangeEvent.CHANGE_UNKNWON:
				break;
		}
	}
		
    @Override
	public void onChangingFailed(final Peripheral peripheral, 
        final PeripheralChangeEvent event) {
        // TODO changing one of peripheral properties failed. Do whatever you want.
        
        final int changeEvent = event.getEvent();
    	switch(changeEvent) {
			case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
				break;
			case PeripheralChangeEvent.CHANGE_MAJOR:
				break;
			case PeripheralChangeEvent.CHANGE_MINOR:
				break;
			case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
				break;
			case PeripheralChangeEvent.CHANGE_TX_POWER:
				break;
			case PeripheralChangeEvent.CHANGE_PASSWORD:
				break;
			case PeripheralChangeEvent.CHANGE_UNKNWON:
				break;
		}
	}
		
    @Override
    @Deprecated
	public void onPasswordChangingCompleted(final Peripheral peripheral) {
	    // This callback method is deprecated. Instead make use of onChangingCompleted()
        // TODO chaing password completed successfuly. Do whatever you want
	}
		
	@Override
	@Deprecated
	public void onPasswordChangingFailed(final Peripheral peripheral) {
	    // This callback method is deprecated. Instead make use of onChangingFailed()
        // TODO changing password failed. Do whatever you want
	}
		
	@Override
	public void onAuthenticatingCompleted(final Peripheral peripheral) {
        // TODO authentication is successfuly completed. Do whatever you want
        // Either use changeMajor() or changeMajorWithDelay(). changeMajorWithDelay() is recommended for safely changing properties
        // writer.changeMajor(100);    // changes major id
        writer.changeMajorWithDelay(100);
	}
		
	@Override
	public void onAuthenticatingFailed(final Peripheral peripheral) {
		// TODO authentication failed. Do whatever you want
	}
};
```

**COPYRIGHT(C) 2014 SK TELECOM. ALL RIGHTS RESERVED.**
