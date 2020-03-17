package com.wh2.redux.sideeffects

import com.wh2.redux.*

typealias SideEffect<State> = (Action, StateAccessor<State>, Dispatch) -> Unit

interface SideEffectsSet<State> {
    fun getSideEffects(): List<SideEffect<State>>
}

fun <S> buildSideEffectsMiddleware(sideEffects: List<SideEffectsSet<S>>): Middleware<S> =
    { state: StateAccessor<S>, action: Action, dispatch: Dispatch, next: Next<S> ->
        next(state, action, dispatch).also {
            sideEffects
                .flatMap { set -> set.getSideEffects() }
                .map { sideEffect -> sideEffect(action, state, dispatch) }
        }
    }