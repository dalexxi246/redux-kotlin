package com.wh2.redux

class DefaultStore<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    private val middlewares: List<Middleware<State>> = listOf()
) : Store<State> {

    private var currentState: State = initialState
    private val subscriptions: ArrayList<Subscription<State>> = arrayListOf()

    override fun getState(): State = currentState

    override fun dispatch(action: Action) {
        val newAction = applyMiddleware(::getState, action)
        val newState = applyReducers(currentState, newAction)
        if (newState == currentState) {
            return
        }
        currentState = newState
        subscriptions.forEach { subscription: Subscription<State> ->
            subscription(newState)
        }
    }

    override fun subscribe(subscription: Subscription<State>): StoreDisposable {
        subscriptions.add(subscription)
        subscription(currentState)
        return StoreDisposable { subscriptions.remove(subscription) }
    }

    private fun applyMiddleware(getState: GetState<State>, action: Action): Action {
        return next(0)(getState, action, ::dispatch)
    }

    private fun next(index: Int): Next<State> {
        if (index == middlewares.size) {
            return { _, action, _ -> action }
        }

        return { state, action, dispatch -> middlewares[index](state, action, dispatch, next(index + 1)) }
    }

    private fun applyReducers(current: State, action: Action): State {
        var newState = current
        newState = reducer(newState, action)
        return newState
    }

}
