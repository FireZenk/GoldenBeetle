package org.firezenk.goldenbleetle.features.btspp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import org.firezenk.goldenbleetle.R
import org.firezenk.goldenbleetle.databinding.ScreenBtsppBinding
import org.koin.android.viewmodel.ext.android.viewModel

class BTSPPScreen : AppCompatActivity() {

    private val viewModel: BTSPPViewModel by viewModel()
    private val binding: ScreenBtsppBinding by lazy { ScreenBtsppBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_btspp)

        binding.acceleration.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel reduce AccelerationChanged(value.toInt())
        }

        binding.steering.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel reduce SteeringChanged(value.toInt())
        }

        viewModel.pullState(this, {
            when (it) {
                is Message -> { }
                is ConnectionStateChanged -> when (it.state) {
                    0 -> { binding.state.text = getString(R.string.state, "CONNECTED") }
                    1 -> { binding.state.text = getString(R.string.state, "DISCONNECTED") }
                    2 -> { binding.state.text = getString(R.string.state, "FAILED") }
                }
            }
        })

        val intent = Intent(applicationContext, DeviceList::class.java)
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
    }

    override fun onStart() {
        super.onStart()
        viewModel reduce StartService
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == RESULT_OK) viewModel reduce Connect(data ?: Intent())
        }
    }

    override fun onStop() {
        viewModel reduce StopService
        super.onStop()
    }
}