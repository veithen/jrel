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
package com.github.veithen.bidiref;

import java.util.Deque;
import java.util.LinkedList;

final class ReverseRelationUpdater<T,U> implements CollectionListener<U> {
    private interface Action {
        <T> boolean execute(MutableReferenceHolder<T> referenceHolder, T object);
    }

    private static final ThreadLocal<Deque<MutableReferenceHolder<?>>> firingReferenceHolders = new ThreadLocal<Deque<MutableReferenceHolder<?>>>() {
        @Override
        protected LinkedList<MutableReferenceHolder<?>> initialValue() {
            return new LinkedList<>();
        }
    };

    private static final Action ADD = new Action() {
        @Override
        public <T> boolean execute(MutableReferenceHolder<T> referenceHolder, T object) {
            return referenceHolder.add(object);
        }
    };

    private static final Action REMOVE = new Action() {
        @Override
        public <T> boolean execute(MutableReferenceHolder<T> referenceHolder, T object) {
            return referenceHolder.remove(object);
        }
    };

    private final T owner;
    private final Relation<U,T> reverseRelation;
    private final MutableReferenceHolder<U> referenceHolder;

    ReverseRelationUpdater(T owner, Relation<U,T> reverseRelation, MutableReferenceHolder<U> referenceHolder) {
        this.owner = owner;
        this.reverseRelation = reverseRelation;
        this.referenceHolder = referenceHolder;
    }

    private void update(Action action, U object) {
        MutableReferenceHolder<T> referenceHolderToUpdate = reverseRelation.getReferenceHolder(object);
        Deque<MutableReferenceHolder<?>> firingReferenceHolders = ReverseRelationUpdater.firingReferenceHolders.get();
        if (firingReferenceHolders.peek() != referenceHolderToUpdate) {
            firingReferenceHolders.push(referenceHolder);
            try {
                if (!action.execute(referenceHolderToUpdate, owner)) {
                    throw new IllegalStateException();
                }
            } finally {
                firingReferenceHolders.pop();
            }
        }
    }

    public void added(U object) {
        update(ADD, object);
    }

    public void removed(U object) {
        update(REMOVE, object);
    }
}
