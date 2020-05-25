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
package com.github.veithen.jrel.transitive;

import com.github.veithen.jrel.ReferenceHolder;
import com.github.veithen.jrel.UnmodifiableReferences;
import com.github.veithen.jrel.association.MutableReference;
import com.github.veithen.jrel.collection.ListenableSet;
import com.github.veithen.jrel.collection.SetListener;

final class TransitiveReferences<T> extends UnmodifiableReferences<T> {
    private final TransitiveClosure<T> closure;
    private final ReferenceHolder<T> referenceHolder;

    TransitiveReferences(TransitiveClosure<T> closure, T owner) {
        super(owner);
        this.closure = closure;
        referenceHolder = closure.getRelation().getReferenceHolder(owner);
        if (closure.isIncludeSelf()) {
            set.add(owner);
        }
        SetListener<T> transitiveReferencesListener = new SetListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
            }
        };
        SetListener<T> directReferenceListener = new SetListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
                ListenableSet<T> transitiveReferences = closure.getReferenceHolder(object).asSet();
                set.addAll(transitiveReferences);
                transitiveReferences.addListener(transitiveReferencesListener);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
                ListenableSet<T> transitiveReferences = closure.getReferenceHolder(object).asSet();
                transitiveReferences.forEach(TransitiveReferences.this::maybeRemove);
                transitiveReferences.removeListener(transitiveReferencesListener);
            }
        };
        referenceHolder.asSet().addListener(directReferenceListener);
        referenceHolder.asSet().forEach(directReferenceListener::added);
    }

    private void maybeRemove(T object) {
        // Optimization if the underlying association is many-to-one.
        if (!(referenceHolder instanceof MutableReference)) {
            if (referenceHolder.asSet().contains(object)) {
                return;
            }
            for (T reference : referenceHolder.asSet()) {
                if (closure.getReferenceHolder(reference).contains(object)) {
                    return;
                }
            }
        }
        set.remove(object);
    }
}
