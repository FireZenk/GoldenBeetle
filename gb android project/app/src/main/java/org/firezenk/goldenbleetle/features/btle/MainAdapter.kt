package org.firezenk.goldenbleetle.features.btle

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.firezenk.goldenbleetle.R

class MainAdapter : ListAdapter<BluetoothDevice, MainAdapter.ViewHolder>(ItemDiffCallback()) {

    private var action: ((BluetoothDevice) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val address: TextView = itemView.findViewById(R.id.deviceAddress)
        private val description: TextView = itemView.findViewById(R.id.deviceDescription)

        @SuppressLint("SetTextI18n")
        fun bind(model: BluetoothDevice, action: ((BluetoothDevice) -> Unit)?) {
            address.text = model.address
            description.text = "${model.name}, ${model.type}, ${model.bondState}"
            itemView.setOnClickListener {
                action?.invoke(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(R.layout.screen_main_adapter_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bind(getItem(position), action)

    fun onItemClick(action: (BluetoothDevice) -> Unit) {
        this.action = action
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<BluetoothDevice>() {

        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem == newItem
        }
    }
}