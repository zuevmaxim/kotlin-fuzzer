package ru.example.kotlinfuzzer.fuzzing

interface InputHandler : Runnable, Comparable<InputHandler> {
    fun priority(): Int
    override fun compareTo(other: InputHandler) = other.priority() - priority()
}
