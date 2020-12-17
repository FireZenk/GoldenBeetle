package org.firezenk.goldenbleetle.features.btspp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import org.firezenk.goldenbleetle.R
import org.koin.android.viewmodel.ext.android.viewModel

class BTSPPScreen : AppCompatActivity() {

    private val viewModel: BTSPPViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_btspp)

        val intent = Intent(applicationContext, DeviceList::class.java)
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == RESULT_OK) viewModel reduce Connect(data ?: Intent())
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                viewModel reduce StartService
            }
        }
    }

    override fun onStop() {
        viewModel reduce StopService
        super.onStop()
    }
}