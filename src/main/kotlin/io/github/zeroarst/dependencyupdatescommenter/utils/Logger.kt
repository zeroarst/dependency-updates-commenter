package io.github.zeroarst.dependencyupdatescommenter.utils

import org.slf4j.LoggerFactory


const val LOGGER_NAME = "duc-log"

fun getDucLogger(prefix: String) = LoggerFactory.getLogger("$LOGGER_NAME:$prefix")
