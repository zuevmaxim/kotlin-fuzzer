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
import java.util.Map;

/** Base interface for all tracers. The primary implementation is {@link Instrumenting}. */
interface Tracer {

  /** Start a trace on the given thread. This should throw if already being traced. */
  void startTrace(Thread thread);

  /**
   * Stop the trace on the given thread. If there is not a trace on the given thread, this should return null. The
   * resulting array from this trace should be sorted using {@link BranchHit}'s set ordering.
   */
  BranchHit[] stopTrace(Thread thread);

  /** Main tracer using instrumenting */
  class Instrumenting implements Tracer {
    @Override
    public void startTrace(Thread thread) {
      BranchTracker.beginTrackingForThread(thread);
    }

    @Override
    public BranchHit[] stopTrace(Thread thread) {
      BranchTracker.BranchHits hits = BranchTracker.endTrackingForThread(thread);
      if (hits == null) return null;
      BranchHit[] ret = new BranchHit[hits.branchHashHits.size()];
      int index = 0;
      for (Map.Entry<Integer, BranchTracker.IntRef> hit : hits.branchHashHits.entrySet()) {
        ret[index++] = new BranchHit(hit.getKey(), hit.getValue().value);
      }
      Arrays.sort(ret);
      return ret;
    }
  }
}