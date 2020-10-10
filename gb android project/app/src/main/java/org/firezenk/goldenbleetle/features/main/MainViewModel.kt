package org.firezenk.goldenbleetle.features.main

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import org.firezenk.goldenbleetle.features.common.MVIViewModel
import org.firezenk.goldenbleetle.system.BluetoothConnection

class MainViewModel(private val bluetoothScanner: BluetoothLeScanner,
                    private val bluetoothConnection: BluetoothConnection)
    : MVIViewModel<MainAction, MainState>() {

    companion object {
        private val TAG = MainViewModel::class.simpleName
        private const val DEVICE_NAME_FILTER = "Bluno"
    }

    private val deviceList = mutableListOf<BluetoothDevice>()

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.name.startsWith(DEVICE_NAME_FILTER)) {
                deviceList.add(result.device)
                DevicesDiscovered(deviceList).pushState()
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            val devices = results.map { it.device }
                .filter { it.name.startsWith(DEVICE_NAME_FILTER) }
            if (devices.isNotEmpty()) {
                deviceList.addAll(devices)
                DevicesDiscovered(deviceList).pushState()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "Error code $errorCode")
        }
    }

    override fun reduce(action: MainAction) {
        when(action) {
            Scan -> onScan()
            Stop -> onStop()
            is Connect -> onConnect(action.device)
            Disconnect -> onDisconnect()
        }
    }

    private fun onScan() = bluetoothScanner.startScan(scanCallback)

    private fun onStop() = bluetoothScanner.stopScan(scanCallback)

    private fun onConnect(device: BluetoothDevice) = bluetoothConnection.makeConnection(device)

    private fun onDisconnect() = bluetoothConnection.disconnect()
}