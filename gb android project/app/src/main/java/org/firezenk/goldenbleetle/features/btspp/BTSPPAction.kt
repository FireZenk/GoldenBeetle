package org.firezenk.goldenbleetle.features.btspp

import android.content.Intent
import org.firezenk.goldenbleetle.features.common.Action

sealed class BTSPPAction : Action()
class Connect(val data: Intent) : BTSPPAction()
object StartService : BTSPPAction()
object StopService : BTSPPAction()
class AccelerationChanged(val value: Int) : BTSPPAction()
class SteeringChanged(val value: Int) : BTSPPAction()