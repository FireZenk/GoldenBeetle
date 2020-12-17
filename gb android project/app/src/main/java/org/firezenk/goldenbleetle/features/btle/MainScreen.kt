package org.firezenk.goldenbleetle.features.btle

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.screen_main.*
import org.firezenk.goldenbleetle.R
import org.koin.android.viewmodel.ext.android.viewModel

class MainScreen : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_BLUETOOTH_PERMISSION = 101
    }

    private val viewModel: MainViewModel by viewModel()

    private val adapter: MainAdapter by lazy { MainAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_main)

        adapter.onItemClick {
            viewModel reduce Stop
            viewModel reduce Connect(it)
        }
        deviceList.adapter = adapter

        numberToSend.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val number = numberToSend.text.toString().toInt()
                viewModel reduce ChangeFunction(number)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.pullState(this, {
            when (it) {
                is DevicesDiscovered -> onDeviceDiscovered(it)
                is ConnectionChanged -> onConnectionChanged(it)
                BluetoothIsDisabled -> requestBluetooth()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel reduce Scan
    }

    override fun onStop() {
        viewModel reduce Disconnect
        super.onStop()
    }

    private fun onDeviceDiscovered(it: DevicesDiscovered) {
        adapter.submitList(it.devices)
    }

    private fun onConnectionChanged(it: ConnectionChanged) {
        connectionText.text = it.state
    }

    private fun requestBluetooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, REQUEST_CODE_BLUETOOTH_PERMISSION)
    }
}