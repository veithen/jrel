/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 - 2022 Andreas Veithen
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
package com.github.veithen.jrel.composition;

import com.github.veithen.jrel.ReferenceHolder;
import com.github.veithen.jrel.UnmodifiableReferences;
import com.github.veithen.jrel.collection.ListenableSet;
import com.github.veithen.jrel.collection.SetListener;

final class CompositeReferences<T1, T2, T3> extends UnmodifiableReferences<T3> {
    private final CompositionRelation<T1, T2, T3> relation;
    private final ReferenceHolder<T2> referenceHolder;

    CompositeReferences(CompositionRelation<T1, T2, T3> relation, T1 owner) {
        this.relation = relation;
        referenceHolder = relation.getRelation1().getReferenceHolder(owner);
        SetListener<T3> relation2Listener =
                new SetListener<T3>() {
                    @Override
                    public void added(T3 object) {
                        set.add(object);
                    }

                    @Override
                    public void removed(T3 object) {
                        maybeRemove(object);
                    }
                };
        SetListener<T2> relation1Listener =
                new SetListener<T2>() {
                    @Override
                    public void added(T2 object) {
                        ListenableSet<T3> refs =
                                relation.getRelation2().getReferenceHolder(object).asSet();
                        set.addAll(refs);
                        refs.addListener(relation2Listener);
                    }

                    @Override
                    public void removed(T2 object) {
                        ListenableSet<T3> refs =
                                relation.getRelation2().getReferenceHolder(object).asSet();
                        refs.forEach(CompositeReferences.this::maybeRemove);
                        refs.removeListener(relation2Listener);
                    }
                };
        referenceHolder.asSet().addListener(relation1Listener);
        referenceHolder.asSet().forEach(relation1Listener::added);
    }

    private void maybeRemove(T3 object) {
        for (T2 reference : referenceHolder.asSet()) {
            if (relation.getRelation2().getReferenceHolder(reference).asSet().contains(object)) {
                return;
            }
        }
        set.remove(object);
    }
}
