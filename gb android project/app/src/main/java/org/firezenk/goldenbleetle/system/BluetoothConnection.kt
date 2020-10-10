package org.firezenk.goldenbleetle.system

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import org.firezenk.goldenbleetle.system.BluetoothLEService.Companion.ACTION_DATA_AVAILABLE
import org.firezenk.goldenbleetle.system.BluetoothLEService.Companion.ACTION_GATT_CONNECTED
import org.firezenk.goldenbleetle.system.BluetoothLEService.Companion.ACTION_GATT_DISCONNECTED
import org.firezenk.goldenbleetle.system.BluetoothLEService.Companion.ACTION_GATT_SERVICES_DISCOVERED
import org.firezenk.goldenbleetle.system.BluetoothLEService.Companion.EXTRA_DATA
import java.util.*

class BluetoothConnection(private val context: Context) {
    
    companion object {
        private const val BLUNO_BEETLE_1_ID = "DF BLUNO"
        const val SerialPortUUID = "0000dfb1-0000-1000-8000-00805f9b34fb"
        const val CommandUUID = "0000dfb2-0000-1000-8000-00805f9b34fb"
        const val ModelNumberStringUUID = "00002a24-0000-1000-8000-00805f9b34fb"
    }

    private var bluetoothLEService: BluetoothLEService? = null
    private var sCharacteristic: BluetoothGattCharacteristic? = null
    private var modelNumberCharacteristic: BluetoothGattCharacteristic? = null
    private var serialPortCharacteristic: BluetoothGattCharacteristic? = null
    private var commandCharacteristic: BluetoothGattCharacteristic? = null
    
    private val gattUpdateReceiver = object : BroadcastReceiver() {
        
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                ACTION_GATT_CONNECTED -> { /* ok */ }
                ACTION_GATT_DISCONNECTED -> { /* ko */ }
                ACTION_GATT_SERVICES_DISCOVERED -> {
                    getGattServices(bluetoothLEService?.getSupportedServices())
                }
                ACTION_DATA_AVAILABLE -> {
                    if (sCharacteristic == modelNumberCharacteristic) {
                        val data = intent.getStringExtra(EXTRA_DATA)
                            ?.toUpperCase(Locale.getDefault())
                        if (data != null && data.startsWith(BLUNO_BEETLE_1_ID)) {
                            setupDevice()
                        }
                    } else {
                        // TODO THIS IS THE DATA STREAM FROM DEVICE
                    }
                }
            }
        }
    }

    fun makeConnection(bluetoothDevice: BluetoothDevice) {
        val gattServiceIntent = Intent(context, BluetoothLEService::class.java)
        context.bindService(gattServiceIntent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                bluetoothLEService = (service as BluetoothLEService.LocalBinder).service
                bluetoothLEService?.connect(bluetoothDevice)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                bluetoothLEService = null
            }

        }, AppCompatActivity.BIND_AUTO_CREATE)

        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    fun disconnect() = bluetoothLEService?.disconnect()

    private fun makeGattUpdateIntentFilter(): IntentFilter = IntentFilter().apply {
        addAction(ACTION_GATT_CONNECTED)
        addAction(ACTION_GATT_DISCONNECTED)
        addAction(ACTION_GATT_SERVICES_DISCOVERED)
        addAction(ACTION_DATA_AVAILABLE)
    }

    private fun getGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return

        gattServices.forEach { service ->
            service.characteristics.forEach { characteristic ->
                when (val uuid = characteristic.uuid.toString()) {
                    ModelNumberStringUUID -> {
                        modelNumberCharacteristic = characteristic
                        println("ModelNumberCharacteristic $uuid")
                    }
                    SerialPortUUID -> {
                        serialPortCharacteristic = characteristic
                        println("SerialPortCharacteristic $uuid")
                    }
                    CommandUUID -> {
                        commandCharacteristic = characteristic
                        println("mSerialPortCharacteristic  $uuid")
                    }
                }
            }
        }
        
        if (modelNumberCharacteristic != null && serialPortCharacteristic != null 
            && commandCharacteristic != null) {
            sCharacteristic = modelNumberCharacteristic
            bluetoothLEService?.setCharacteristicNotification(sCharacteristic, true)
            bluetoothLEService?.readCharacteristic(sCharacteristic)
        }
    }

    private fun setupDevice() {
        bluetoothLEService?.setCharacteristicNotification(sCharacteristic, false)
        sCharacteristic = commandCharacteristic
        sCharacteristic?.setValue("AT+PASSWOR=DFRobot\r\n")
        bluetoothLEService?.writeCharacteristic(sCharacteristic)
        sCharacteristic?.setValue("AT+CURRUART=${115200}\r\n")
        bluetoothLEService?.writeCharacteristic(sCharacteristic)
        sCharacteristic = serialPortCharacteristic
        bluetoothLEService?.setCharacteristicNotification(sCharacteristic, true)
    }
}