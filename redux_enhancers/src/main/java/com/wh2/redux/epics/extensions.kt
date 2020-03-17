package com.wh2.redux.epics

import com.wh2.redux.Action
import io.reactivex.Observable

inline fun <reified R : Any> Observable<in Action>.ofType(): Observable<R> = ofType(R::class.java)

inline fun <reified R > Observable<R>.dismissActions(): Observable<Action> = flatMap { Observable.empty<Action>() }