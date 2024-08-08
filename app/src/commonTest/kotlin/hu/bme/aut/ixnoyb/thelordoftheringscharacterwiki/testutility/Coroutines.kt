package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility

internal const val DEFAULT_DISPATCHER_THREAD_NAME_PREFIX = "DefaultDispatcher-worker-"

internal const val WAITING_FOR_TEST_BACKGROUND_TASK_DELAY = 5L

internal expect fun getThreadName(): String