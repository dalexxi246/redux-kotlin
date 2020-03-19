package com.wh2.redux

typealias SubStateFilter<State, SubState> = (State) -> SubState

fun <State, SubState> Store<State>.subscribe(
    subStateFilter: SubStateFilter<State, SubState>,
    subscription: (SubState) -> Unit
): Unsubscribe {

    return subscribe { newState: State ->

        val newSubState = subStateFilter(newState)
        subscription(newSubState)

    }
}
