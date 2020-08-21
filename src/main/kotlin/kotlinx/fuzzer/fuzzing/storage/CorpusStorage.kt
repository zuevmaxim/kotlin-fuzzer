package kotlinx.fuzzer.fuzzing.storage

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import java.util.*

/**
 * Limited linked set of corpus inputs.
 * @param limit maximum size of corpus storage
 */
class CorpusStorage(limit: Int) : Iterable<ExecutedInput> {
    private val corpus = ConcurrentLinkedHashMap.Builder<ExecutedInput, Boolean>()
        .maximumWeightedCapacity(limit.toLong())
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
}
