package org.firezenk.goldenbleetle

import android.app.Application
import android.bluetooth.BluetoothManager
import org.firezenk.goldenbleetle.features.btle.MainViewModel
import org.firezenk.goldenbleetle.features.btspp.BTSPPViewModel
import org.firezenk.goldenbleetle.system.BluetoothConnection
import org.firezenk.goldenbleetle.system.BluetoothSPPService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val mainModule = module {
            single { BluetoothConnection(get()) }
            single { BluetoothSPPService(get()) }
            viewModel { MainViewModel(bluetoothAdapter, get()) }
            viewModel { BTSPPViewModel(get()) }
        }

        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(mainModule)
        }
    }
}