package org.firezenk.goldenbleetle.system

import android.content.Context
import android.content.Intent
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState

class BluetoothSPPService(context: Context) {

    private var spp = BluetoothSPP(context)

    fun connect(data: Intent) = spp.connect(data)

    fun connectionState(stateCallback: (Int) -> Unit) =
        spp.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String?, address: String?) = stateCallback(0)
            override fun onDeviceDisconnected() = stateCallback(1)
            override fun onDeviceConnectionFailed() = stateCallback(2)
        })

    fun startService() {
        spp.setupService()
        spp.startService(BluetoothState.DEVICE_OTHER)
    }

    fun stopService() = spp.stopService()

    fun acceleration(value: Int) = spp.send("A $value", true)

    fun steering(value: Int) = spp.send("S $value", true)
}