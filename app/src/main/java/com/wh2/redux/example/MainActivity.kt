package com.wh2.redux.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wh2.redux.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var storeDisposable: StoreDisposable
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val reducer: Reducer<Int> = { state, action ->
        when (action) {
            is ExampleAction.Sum -> state.plus(action.value)
            is ExampleAction.Minus -> state.minus(action.value)
            is ExampleAction.Multiply -> state.times(action.value)
            is ExampleAction.MinusTenTh -> state.minus(action.value)
            else -> state
        }
    }

    private val loggingMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        Log.i("BeforeAction", "$action")
        val newAction = next(state, action, dispatch)
        Log.i("AfterAction", "$newAction")
        action
    }

    private val coroutinesAsyncMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        when (action) {
            ExampleAction.Sum(1) -> {
                launch {
                    val int = RandomNumberFetcher.fetchRandomAsync().await()
                    dispatch(ExampleAction.Multiply(int))
                }
            }
            is ExampleAction.Minus -> {
                launch {
                    val int = RandomNumberFetcher.fetchTenThousandsAsync().await()
                    dispatch(ExampleAction.MinusTenTh(int))
                }
            }
        }
        next(state, action, dispatch)
    }

    private val multiplyCatcherMiddleware: Middleware<Int> = { state, action, dispatch, next ->
        if (action is ExampleAction.Multiply) {
            Toast.makeText(this, "Multiply ${state()} by ${action.value}", Toast.LENGTH_SHORT)
                .show()
        }
        next(state, action, dispatch)
    }

    private val reduxStore = DefaultStore(
        initialState = 0,
        reducer = reducer,
        middlewares = listOf(
            loggingMiddleware,
            coroutinesAsyncMiddleware,
            multiplyCatcherMiddleware
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storeDisposable = reduxStore.subscribe { new ->
            textView_result.text = "$new"
        }
        button_sum.setOnClickListener { reduxStore.dispatch(ExampleAction.Sum(1)) }
        button_minus.setOnClickListener { reduxStore.dispatch(ExampleAction.Minus(1)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        storeDisposable.dispose()
    }

}

sealed class ExampleAction : Action {
    data class Sum(val value: Int) : ExampleAction()
    data class Minus(val value: Int) : ExampleAction()
    data class Multiply(val value: Int) : ExampleAction()
    data class MinusTenTh(val value: Int): ExampleAction()
}