package com.wh2.redux.sideeffects

import com.wh2.redux.*

typealias SideEffect<State> = (Action, GetState<State>, Dispatch) -> Unit

interface SideEffectsSet<State> {
    fun getSideEffects(): List<SideEffect<State>>
}

fun <S> buildSideEffectsMiddleware(sideEffects: List<SideEffectsSet<S>>): Middleware<S> =
    { getState: GetState<S>, action: Action, dispatch: Dispatch, next: Next<S> ->
        next(getState, action, dispatch).also {
            sideEffects
                .flatMap { set -> set.getSideEffects() }
                .map { sideEffect -> sideEffect(action, getState, dispatch) }
        }
    }