package org.firezenk.goldenbleetle.features.common

class Store {

    private val states: MutableList<State> = mutableListOf()

    val frozenStates: List<State>
        get() = states.toList()

    internal fun add(function: () -> State) {
        states.add(function())
    }

    internal fun clear() = states.clear()

    inline fun <reified S> provideLast(): S? = frozenStates.findLast { it is S } as S?
}