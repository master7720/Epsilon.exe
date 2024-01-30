package club.eridani.epsilon.client

import club.eridani.epsilon.client.util.threads.mainScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

internal object LoaderWrapper {
    private val loaderList = ArrayList<AsyncLoader<*>>()

    @JvmStatic
    fun preLoadAll() {
        loaderList.forEach { it.preLoad() }
    }

    @JvmStatic
    fun loadAll() {
        runBlocking {
            loaderList.forEach { it.load() }
        }
    }
}

internal interface AsyncLoader<T> {
    var deferred: Deferred<T>?

    fun preLoad() {
        deferred = preLoadAsync()
    }

    private fun preLoadAsync(): Deferred<T> {
        return mainScope.async { preLoad0() }
    }

    suspend fun load() {
        load0((deferred ?: preLoadAsync()).await())
    }

    suspend fun preLoad0(): T
    suspend fun load0(input: T)

    companion object {

    }
    }