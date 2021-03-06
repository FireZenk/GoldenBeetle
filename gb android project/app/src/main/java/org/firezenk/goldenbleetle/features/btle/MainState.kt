package org.firezenk.goldenbleetle.features.btle

import android.bluetooth.BluetoothDevice
import org.firezenk.goldenbleetle.features.common.State

sealed class MainState : State()
class DevicesDiscovered(val devices: List<BluetoothDevice>) : MainState()
class ConnectionChanged(val state: String) : MainState()
object BluetoothIsDisabled : MainState()