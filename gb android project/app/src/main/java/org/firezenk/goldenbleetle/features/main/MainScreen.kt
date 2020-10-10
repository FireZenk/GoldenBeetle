package org.firezenk.goldenbleetle.features.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.screen_main.*
import org.firezenk.goldenbleetle.R
import org.koin.android.viewmodel.ext.android.viewModel

class MainScreen : AppCompatActivity() {

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

        viewModel reduce Scan

        viewModel.pullState(this, {
            when (it) {
                is DevicesDiscovered -> onDeviceDiscovered(it)
            }
        })
    }

    private fun onDeviceDiscovered(it: DevicesDiscovered) {
        adapter.submitList(it.devices)
    }
}