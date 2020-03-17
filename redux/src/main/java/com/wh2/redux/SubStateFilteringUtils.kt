package com.wh2.redux

typealias SubStateFilter<State, SubState> = (State) -> SubState

fun <State, SubState> Store<State>.subscribe(subStateFilter: SubStateFilter<State, SubState>, subscription: (SubState) -> Unit) : Unsubscribe {

    return subscribe { oldState: State, newState: State ->

        val oldSubState = subStateFilter(oldState)
        val newSubState = subStateFilter(newState)
        if (oldSubState != newSubState) {
            subscription(newSubState)
        }

    }
}
