package org.firezenk.goldenbleetle.features.btle

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import org.firezenk.goldenbleetle.features.common.MVIViewModel
import org.firezenk.goldenbleetle.system.BluetoothConnection

class MainViewModel(private val bluetoothAdapter: BluetoothAdapter,
                    private val bluetoothConnection: BluetoothConnection)
    : MVIViewModel<MainAction, MainState>() {

    companion object {
        private val TAG = MainViewModel::class.simpleName
        private const val DEVICE_NAME_FILTER = "Bluno"
    }

    private val deviceList = mutableListOf<BluetoothDevice>()
    private var ledIsActivated = false

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.name != null && result.device.name.startsWith(DEVICE_NAME_FILTER)) {
                deviceList.add(result.device)
                DevicesDiscovered(deviceList).pushState()
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            val devices = results.map { it.device }
                .filter { it.name != null && it.name.startsWith(DEVICE_NAME_FILTER) }
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
            Scan -> {
                if (bluetoothAdapter.isEnabled)
                    onScan()
                else
                    BluetoothIsDisabled.pushState()
            }
            Stop -> onStop()
            is Connect -> onConnect(action.device)
            Disconnect -> onDisconnect()
            is ChangeFunction -> onFunctionChanged(action.number)
        }
    }

    private fun onScan() = bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)

    private fun onStop() = bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)

    private fun onConnect(device: BluetoothDevice) {
        bluetoothConnection.makeConnection(device) { ConnectionChanged(it).pushState() }
    }

    private fun onDisconnect() = bluetoothConnection.disconnect()

    private fun onFunctionChanged(number: Int) {
        ledIsActivated = !ledIsActivated
        bluetoothConnection.changeFunction(number)
    }
}