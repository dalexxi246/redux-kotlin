package com.wh2.redux.epics

import com.wh2.redux.*
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject

typealias Epic<State> = (Observable<in Action>, GetState<State>) -> Observable<out Action>

class EpicsMiddleware<State>(private val epics: List<Epic<State>>) : Middleware<State> {

    private val actionsStream: PublishSubject<Action> = PublishSubject.create()
    private val epicsDisposable: SerialDisposable = SerialDisposable()

    override fun invoke(getState: GetState<State>, action: Action, dispatch: Dispatch, next: Next<State>): Action {
        return next(getState, action, dispatch).also {
            epicsDisposable.set(
                combineEpics(actionsStream, getState, epics).subscribe(
                    dispatch,
                    Throwable::printStackTrace
                )
            )
            actionsStream.onNext(it)
        }
    }

    private fun combineEpics(
        actions: Observable<Action>,
        getState: GetState<State>,
        epics: List<Epic<State>>
    ): Observable<Action> {
        return Observable.merge(epics.map { it(actions, getState) })
    }

}
