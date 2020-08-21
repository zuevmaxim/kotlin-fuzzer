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
import java.util.concurrent.ConcurrentHashMap;

/** Internally used class that is called by branch tracking operations. */
public class BranchTracker {

    /** Debug information. */
    public static final ConcurrentHashMap<Integer, String> indexToClass = new ConcurrentHashMap<>();

    /** A reference to the addBranchHash method. */
    static final MethodReference ref;

    private static final ThreadLocal<BranchHits> branchHits = ThreadLocal.withInitial(() -> null);

    static {
        Method method = null;
        try {
            method = BranchTracker.class.getDeclaredMethod("addBranchHash", int.class);
        } catch (NoSuchMethodException ignored) {
            assert false;
        }
        ref = new MethodReference(method);
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
        branchHits.remove();
        return hits;
    }

    /** Internal helper to add a branch hash for the current thread. */
    public static void addBranchHash(int branchHash) {
        BranchHits hits = branchHits.get();
        if (hits != null) hits.addHit(branchHash);
    }

}
