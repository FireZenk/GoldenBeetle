package org.firezenk.goldenbleetle.features.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

abstract class MVIViewModel<A: Action, S: State>(val store: Store = Store()): ViewModel() {

    private val state: MutableLiveData<S> = MutableLiveData()

    abstract infix fun reduce(action: A)

    fun S.pushState() {
        store.add { this }
        state.value = this
    }

    fun pullState(owner: LifecycleOwner, observer: (S) -> Unit) {
        state.observe(owner, { observer(it) })
    }

    fun destroy() {
        store.clear()
    }
}