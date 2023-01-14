package io.github.zeroarst.dependencyupdatescommenter

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import kotlin.jvm.Throws

val ducLogger = Logging.getLogger("duc-log")

// val ducLogger = Logger("[duc-log] ", Logging.getLogger("duc-log"))

class Logger(private val prefix: String? = null, private val logger: Logger) {
    fun d(s: String?) {
        logger.debug("$prefix$s")
    }

    fun d(s: String, throwable: Throwable) {
        logger.debug("$prefix$s", throwable)
    }

    fun w(s: String?) {
        logger.warn("$prefix$s")
    }

    fun w(s: String, throwable: Throwable) {
        logger.warn("$prefix$s", throwable)
    }

    fun e(s: String?) {
        logger.error("$prefix$s")
    }

    fun e(s: String, throwable: Throwable) {
        logger.error("$prefix$s", throwable)
    }
}



