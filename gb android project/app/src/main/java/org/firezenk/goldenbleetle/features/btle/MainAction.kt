package org.firezenk.goldenbleetle.features.btle

import android.bluetooth.BluetoothDevice
import org.firezenk.goldenbleetle.features.common.Action

sealed class MainAction : Action()
object Scan : MainAction()
object Stop : MainAction()
class Connect(val device: BluetoothDevice) : MainAction()
object Disconnect : MainAction()
class ChangeFunction(val number: Int) : MainAction()