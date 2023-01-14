package io.github.zeroarst.dependencyupdatescommenter.utils

abstract class Executer<I, O> {
    abstract fun execute(input: I): O
}


