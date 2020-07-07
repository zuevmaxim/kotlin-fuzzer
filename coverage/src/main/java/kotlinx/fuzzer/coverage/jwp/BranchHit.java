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

import java.util.Arrays;
import java.util.Objects;

/**
 * Representation of a single branching operation
 */
public class BranchHit implements Comparable<BranchHit> {

    /**
     * The hash unique to this branch
     */
    public final int branchHash;

    /**
     * The number of times the branch was executed
     */
    public final int hitCount;

    /**
     * The hash of {@link #branchHash} and {@link #hitBucket()}
     */
    public final int withHitCountHash;

    public BranchHit(int branchHash, int hitCount) {
        this.branchHash = branchHash;
        this.hitCount = hitCount;
        withHitCountHash = Objects.hash(branchHash, hitBucket());
    }

    /**
     * Returns the number of hits bucketed into a value of 1, 2, 3, 4, 8, 16, 32, or 128
     */
    public int hitBucket() {
        if (hitCount < 4) return hitCount;
        if (hitCount < 8) return 4;
        if (hitCount < 16) return 8;
        if (hitCount < 32) return 16;
        if (hitCount < 128) return 32;
        return 128;
    }

    /**
     * Compares using {@link #branchHash}
     */
    @Override
    public int compareTo(BranchHit o) {
        // Hit counts don't factor in because they don't affect uniqueness
        return o == null ? -1 : Integer.compare(branchHash, o.branchHash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchHit branchHit = (BranchHit) o;
        return branchHash == branchHit.branchHash &&
                hitCount == branchHit.hitCount &&
                withHitCountHash == branchHit.withHitCountHash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchHash, hitCount, withHitCountHash);
    }

    /**
     * Interface for hashing {@link BranchHit}s
     */
    @FunctionalInterface
    public interface Hasher {
        /**
         * Uses {@link BranchHit#withHitCountHash}
         */
        Hasher WITH_HIT_COUNTS = hit -> hit.withHitCountHash;
        /**
         * Uses {@link BranchHit#branchHash}
         */
        Hasher WITHOUT_HIT_COUNTS = hit -> hit.branchHash;

        /**
         * Create unique hash for the given branch
         */
        int hash(BranchHit hit);

        /**
         * Create single hash for all given branches together. The parameter is expected to be sorted by the caller before
         * invoking.
         * <p>
         * The default implementation just uses {@link #hash(BranchHit)} and then {@link Arrays#hashCode(int[])}
         */
        default int hash(BranchHit... hits) {
            int[] hashes = new int[hits.length];
            for (int i = 0; i < hits.length; i++) hashes[i] = hash(hits[i]);
            return Arrays.hashCode(hashes);
        }
    }
}
