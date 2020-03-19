package com.wh2.redux.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wh2.redux.Action
import com.wh2.redux.DefaultStore
import com.wh2.redux.Middleware
import com.wh2.redux.Reducer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val reducer: Reducer<Int> = { state, action ->
        when (action) {
            is ExampleAction.Sum -> state.plus(action.value)
            is ExampleAction.Minus -> state.minus(action.value)
            is ExampleAction.Multiply -> state.times(action.value)
            else -> state
        }
    }

    private val loggingMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        Log.i("BeforeState", "${state()}")
        Log.i("ReduxAction", action.toString())
        next(state, action, dispatch)
    }

    private val coroutinesAsyncMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        if (action == ExampleAction.Sum(1)) {
            launch {
                delay(1000)
                dispatch(ExampleAction.Multiply(3))
            }
            next(state, action, dispatch)
        } else {
            next(state, action, dispatch)
        }
    }

    private val multiplyCatcherMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        if (action is ExampleAction.Multiply) {
            Toast.makeText(this, "Multiply ${state()} by ${action.value}", Toast.LENGTH_SHORT).show()
        }
        next(state, action, dispatch)
    }

    private val reduxStore = DefaultStore(
        initialState = 0,
        reducer = reducer,
        middlewares = listOf(loggingMiddleware, coroutinesAsyncMiddleware, multiplyCatcherMiddleware)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reduxStore.subscribe { new ->
            textView_result.text = "$new"
        }
        button_sum.setOnClickListener { reduxStore.dispatch(ExampleAction.Sum(1)) }
        button_minus.setOnClickListener { reduxStore.dispatch(ExampleAction.Minus(1)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}

sealed class ExampleAction : Action {
    data class Sum(val value: Int) : ExampleAction()
    data class Minus(val value: Int) : ExampleAction()
    data class Multiply(val value: Int): ExampleAction()
}