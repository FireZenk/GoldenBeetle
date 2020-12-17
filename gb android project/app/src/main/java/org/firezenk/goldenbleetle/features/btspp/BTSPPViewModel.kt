package org.firezenk.goldenbleetle.features.btspp

import org.firezenk.goldenbleetle.features.common.MVIViewModel
import org.firezenk.goldenbleetle.system.BluetoothSPPService

class BTSPPViewModel(private val service: BluetoothSPPService)
    : MVIViewModel<BTSPPAction, BTSPPState>() {

    override fun reduce(action: BTSPPAction) {
        when(action) {
            is Connect -> service.connect(action.data)
            StartService -> service.startService()
            StopService ->service.stopService()
        }
    }
}