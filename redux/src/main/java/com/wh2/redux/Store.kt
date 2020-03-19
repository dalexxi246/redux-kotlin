package com.wh2.redux

interface Action
typealias Reducer<State> = (State, Action) -> State
typealias Subscription<NewState> = (NewState) -> Unit
typealias Unsubscribe = () -> Unit

interface Store<State> {

    fun getState() : State
    fun getOldState() : State
    fun dispatch(action: Action)
    fun subscribe(subscription: Subscription<State>) : Unsubscribe

}