/*
 * MIT License
 *
 * Copyright (c) 2018 Chad Retz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package kotlinx.fuzzer.coverage.jwp;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;

/** Internally used class that is called by branch tracking operations */
public class BranchTracker {

    /** The set of method refs for the tracker */
    static final MethodBranchAdapter.MethodRef ref;
    /** Map storing all hits by thread */
    private static final ThreadLocal<BranchHits> branchHits = ThreadLocal.withInitial(() -> null);

    static {
        Method method = null;
        try {
            method = BranchTracker.class.getDeclaredMethod("addBranchHash", int.class);
        } catch (NoSuchMethodException ignored) {
            assert false;
        }
        ref = new MethodBranchAdapter.MethodRef(method);
    }

    /** Start tracking the given thread. This will fail if the thread is already being tracked. */
    static void beginTrackingForThread() {
        if (branchHits.get() != null) {
            throw new IllegalArgumentException("Thread already being tracked");
        }
        branchHits.set(new BranchHits());
    }

    /** Stop tracking the given thread. Returns null if never started. */
    static BranchHits endTrackingForThread() {
        BranchHits hits = branchHits.get();
        branchHits.set(null);
        return hits;
    }

    /** Internal helper to add a branch hash for the current thread */
    public static void addBranchHash(int branchHash) {
        BranchHits hits = branchHits.get();
        // Even though hits isn't thread safe, we know we're safe since it's essentially thread local.
        if (hits != null) hits.addHit(branchHash);
    }

    /** Internal class for holding and incrementing hit counts */
    static class BranchHits {
        public final LinkedHashSet<Integer> branchHashHits = new LinkedHashSet<>();

        /** Add a hit for the given branch count */
        public void addHit(int branchHash) {
            branchHashHits.add(branchHash);
        }
    }
}
