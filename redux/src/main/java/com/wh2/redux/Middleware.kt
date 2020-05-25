package com.wh2.redux

typealias Dispatch = (Action) -> Unit
typealias GetState<State> = () -> State
typealias Next<State> = (GetState<State>, Action, Dispatch) -> Action
typealias Middleware<State> = (getState: GetState<State>, action: Action, dispatch: Dispatch, next: Next<State>) -> Action