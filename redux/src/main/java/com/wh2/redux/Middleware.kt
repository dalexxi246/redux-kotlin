package com.wh2.redux

typealias Dispatch = (Action) -> Unit
typealias StateAccessor<State> = () -> State
typealias Next<State> = (StateAccessor<State>, Action, Dispatch) -> Action
typealias Middleware<State> = (StateAccessor<State>, Action, Dispatch, Next<State>) -> Action