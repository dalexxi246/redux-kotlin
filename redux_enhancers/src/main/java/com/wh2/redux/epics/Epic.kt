package com.wh2.redux.epics

import com.wh2.redux.*
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject

typealias Epic<State> = (Observable<in Action>, StateAccessor<State>) -> Observable<out Action>

class EpicsMiddleware<State>(private val epics: List<Epic<State>>) : Middleware<State> {

    private val actionsStream: PublishSubject<Action> = PublishSubject.create()
    private val epicsDisposable: SerialDisposable = SerialDisposable()

    override fun invoke(state: StateAccessor<State>, action: Action, dispatch: Dispatch, next: Next<State>): Action {
        return next(state, action, dispatch).also {
            epicsDisposable.set(
                combineEpics(actionsStream, state, epics).subscribe(
                    dispatch,
                    Throwable::printStackTrace
                )
            )
            actionsStream.onNext(it)
        }
    }

    private fun combineEpics(
        actions: Observable<Action>,
        state: StateAccessor<State>,
        epics: List<Epic<State>>
    ): Observable<Action> {
        return Observable.merge(epics.map { it(actions, state) })
    }

}
