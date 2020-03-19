package com.wh2.redux.android

import androidx.lifecycle.*
import com.wh2.redux.android.extensions.distinctUntilChanged

abstract class StatefulViewModel<ViewState>(private val initialState: ViewState) : ViewModel() {

    private val stateLiveData: MutableLiveData<ViewState> = MutableLiveData()

    fun getState(): ViewState = stateLiveData.value ?: initialState

    init {
        setState(initialState)
    }

    fun dispatch(action: UIAction) {
        val newState = applyReducers(getState(), action)
        setState(newState)
        onStateChanged().invoke(action, getState())
    }

    protected fun setState(newState: ViewState) {
        stateLiveData.value = newState
    }

    protected open fun applyReducers(oldState: ViewState, action: UIAction): ViewState {
        return oldState
    }

    protected open fun onStateChanged(): ((UIAction, ViewState) -> Unit) = { _, _ ->  }

    open fun subscribe(owner: LifecycleOwner, observer: Observer<ViewState>) {
        stateLiveData.distinctUntilChanged().observe(owner, observer)
    }

    open fun <SubState> subscribe(substate: (ViewState) -> SubState, owner: LifecycleOwner, observer: Observer<SubState>) {
        Transformations
            .map(stateLiveData) { viewState -> substate(viewState) }
            .distinctUntilChanged()
            .observe(owner, observer)
    }

}

interface UIAction