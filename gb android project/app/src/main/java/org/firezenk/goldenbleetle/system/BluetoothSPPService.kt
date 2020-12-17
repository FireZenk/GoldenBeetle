package org.firezenk.goldenbleetle.system

import android.content.Context
import android.content.Intent
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState

class BluetoothSPPService(context: Context) {

    private var spp = BluetoothSPP(context)

    fun connect(data: Intent) = spp.connect(data)

    fun startService() = spp.startService(BluetoothState.DEVICE_OTHER)

    fun stopService() = spp.stopService()
}