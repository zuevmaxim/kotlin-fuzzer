package kotlinx.fuzzer.fuzzing.storage

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import java.util.*

/**
 * Limited linked set of corpus inputs.
 * @param memoryLimitMb maximum memory usage of corpus in Mb
 */
class CorpusStorage(memoryLimitMb: Int) : Iterable<ExecutedInput> {
    private val corpus = ConcurrentLinkedHashMap.Builder<ExecutedInput, Boolean>()
        .maximumWeightedCapacity(megaBytesToBytes(memoryLimitMb))
        .weigher { input, _ -> 64 + input.data.size }
        .build()
        .let { Collections.newSetFromMap(it) }

    val size: Int get() = corpus.size

    override fun iterator() = corpus.iterator()

    fun add(input: ExecutedInput) = corpus.add(input)

    private val iterators = ThreadLocal.withInitial { corpus.iterator() }

    /** Get next input in sequence for this thread. Returns null if corpus is empty. */
    fun next(): ExecutedInput? {
        if (corpus.isEmpty()) return null
        var it = iterators.get()
        if (!it.hasNext()) {
            it = corpus.iterator()
            iterators.set(it)
            check(it.hasNext())
        }
        return it.next()
    }

    private fun megaBytesToBytes(mb: Int) = mb.toLong() shl 20
}
