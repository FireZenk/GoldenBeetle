package org.firezenk.goldenbleetle.system

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BluetoothLEService : Service() {

    companion object {
        private val TAG = BluetoothLEService::class.simpleName
        private const val STATE_DISCONNECTED: Int = 0
        private const val STATE_CONNECTING: Int = 1
        private const val STATE_CONNECTED: Int = 2

        const val ACTION_GATT_CONNECTED =
            "org.firezenk.goldenbleetle.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "org.firezenk.goldenbleetle.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "org.firezenk.goldenbleetle.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "org.firezenk.goldenbleetle.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "org.firezenk.goldenbleetle.EXTRA_DATA"
    }

    private val binder: IBinder = LocalBinder()

    private var connectionState: Int = STATE_DISCONNECTED
    private var deviceAddress: String? = null
    private var bluetoothGatt: BluetoothGatt? = null

    inner class LocalBinder : Binder() {
        val service =  this@BluetoothLEService
    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                connectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                Log.i(TAG, "Attempting to start service discovery: " + gatt.discoverServices())
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                connectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        bluetoothGatt?.close()
        bluetoothGatt = null
        return super.onUnbind(intent)
    }

    fun connect(device: BluetoothDevice): Boolean {
        // Previously connected device. Try to reconnect.
        if (reconnect(device.address)) return true

        deviceAddress = device.address

        // New device. Start new connection
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Log.d(TAG, "Trying to create a new connection.")

        deviceAddress = device.address
        connectionState = STATE_CONNECTING

        return true
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    fun getSupportedServices(): List<BluetoothGattService>? = bluetoothGatt?.services

    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic?,
        enabled: Boolean
    ) = bluetoothGatt?.setCharacteristicNotification(characteristic, enabled)

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?)
            = bluetoothGatt?.readCharacteristic(characteristic)

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?)
            = bluetoothGatt?.writeCharacteristic(characteristic)

    private fun reconnect(address: String): Boolean {
        if (deviceAddress != null && deviceAddress == address && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            return if (bluetoothGatt!!.connect()) {
                connectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }
        return false
    }

    private fun broadcastUpdate(
        action: String,
        characteristic: BluetoothGattCharacteristic? = null
    ) {
        val intent = Intent(action)
        if (characteristic != null) intent.putExtra(EXTRA_DATA, String(characteristic.value))
        sendBroadcast(intent)
    }
}