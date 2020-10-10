package org.firezenk.goldenbleetle.features.main

import android.bluetooth.BluetoothDevice
import org.firezenk.goldenbleetle.features.common.State

sealed class MainState : State()
class DevicesDiscovered(val devices: List<BluetoothDevice>) : MainState()