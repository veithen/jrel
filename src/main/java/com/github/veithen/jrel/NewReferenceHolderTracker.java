/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.jrel;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Keeps track of {@link ReferenceHolder} instances being created so that a call to
 * {@link BinaryRelation#getReferenceHolder(Object)} can successfully return an instance that has
 * not yet been stored yet, i.e. before the corresponding call to
 * {@link BinaryRelation#newReferenceHolder(Object)} returns.
 */
final class NewReferenceHolderTracker {
    private static final ThreadLocal<Deque<NewReferenceHolderTracker>> stack = new ThreadLocal<Deque<NewReferenceHolderTracker>>() {
        @Override
        protected Deque<NewReferenceHolderTracker> initialValue() {
            return new LinkedList<>();
        }
    };

    private final BinaryRelation<?,?,?,?> relation;
    private final Object owner;
    private ReferenceHolder<?> referenceHolder;

    NewReferenceHolderTracker(BinaryRelation<?,?,?,?> relation, Object owner) {
        this.relation = relation;
        this.owner = owner;
    }

    static void push(BinaryRelation<?,?,?,?> relation, Object owner) {
        stack.get().push(new NewReferenceHolderTracker(relation, owner));
    }

    static void created(ReferenceHolder<?> referenceHolder) {
        NewReferenceHolderTracker current = stack.get().peek();
        if (current.referenceHolder != null) {
            throw new IllegalStateException();
        }
        current.referenceHolder = referenceHolder;
    }

    static ReferenceHolder<?> match(BinaryRelation<?,?,?,?> relation, Object owner) {
        for (NewReferenceHolderTracker tracker : stack.get()) {
            if (tracker.relation == relation && tracker.owner == owner) {
                return tracker.referenceHolder;
            }
        }
        return null;
    }

    static void pop() {
        if (stack.get().pop().referenceHolder == null) {
            throw new IllegalStateException();
        }
    }
}
