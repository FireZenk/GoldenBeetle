package org.firezenk.goldenbleetle.features.btspp

import org.firezenk.goldenbleetle.features.common.State

sealed class BTSPPState : State()
class Message(val message: String) : BTSPPState()
class ConnectionStateChanged(val state: Int) : BTSPPState()