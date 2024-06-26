package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/** Replace ascii number with random number. */
internal class ReplaceTextNumberMutation : Mutation {
    data class NumberPosition(val start: Int, val length: Int)

    override fun mutate(bytes: ByteArray): ByteArray? {
        val numberPositions = numberPositions(bytes)
        if (numberPositions.isEmpty()) {
            return null
        }
        val number = numberPositions.random()
        val newNumber = randomNumber().toString().toByteArray()
        return ByteArray(bytes.size - number.length + newNumber.size).also { newBytes ->
            bytes.copyInto(newBytes, startIndex = 0, endIndex = number.start)
            newNumber.copyInto(newBytes, destinationOffset = number.start)
            bytes.copyInto(newBytes, destinationOffset = number.start + newNumber.size, startIndex = number.start + number.length)
        }
    }

    private fun randomNumber() = Random.nextInt(MAX_REPLACE_NUMBER_VALUE, MAX_REPLACE_NUMBER_VALUE + 1)

    private fun numberPositions(bytes: ByteArray): List<NumberPosition> {
        val numbers = mutableListOf<NumberPosition>()
        var position = -1
        var length = 0
        for (i in bytes.indices) {
            if (bytes[i].isDigit()) {
                if (position == -1) {
                    position = i
                    length = 1
                } else {
                    length++
                }
            } else {
                if (position != -1) {
                    numbers.add(NumberPosition(position, length))
                    position = -1
                }
            }
        }
        if (position != -1) {
            numbers.add(NumberPosition(position, length))
        }
        return numbers
    }

}

internal const val MAX_REPLACE_NUMBER_VALUE = 1000
