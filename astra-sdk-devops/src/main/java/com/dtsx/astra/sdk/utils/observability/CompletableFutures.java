package com.dtsx.astra.sdk.utils.observability;

/*-
 * #%L
 * Data API Java Client
 * --
 * Copyright (C) 2024 DataStax
 * --
 * Licensed under the Apache License, Version 2.0
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilities to work with Async functions.
 */
public class CompletableFutures {

    /**
     * Hide constructor in utilities.
     */
    private CompletableFutures() {}

    /**
     * Merge multiple CompletionStage in a single one
     * @param inputs
     *      list of completion stages
     * @return
     *      the merged stage
     * @param <T>
     *      generic used with stages
     */
    public static <T> CompletionStage<Void> allDone(List<CompletionStage<T>> inputs) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        if (inputs.isEmpty()) {
            result.complete(null);
        } else {
            final int todo = inputs.size();
            final AtomicInteger done = new AtomicInteger();
            for (CompletionStage<?> input : inputs) {
                input.whenComplete(
                        (v, error) -> {
                            if (done.incrementAndGet() == todo) {
                                result.complete(null);
                            }
                        });
            }
        }
        return result;
    }
}
