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

public final class Relation<T,U> extends BinaryRelationship<T,U,ReferenceHolder<U>,ReferenceHolder<T>,Relation<T,U>,Relation<U,T>> {
    private final Relation<U,T> reverse;

    Relation(Relation<U,T> reverse) {
        this.reverse = reverse;
    }

    public Relation() {
        reverse = createReverse();
    }

    Relation<U,T> createReverse() {
        return new Relation<U,T>(this);
    }

    public Relation<U,T> getReverse() {
        return reverse;
    }

    private void addListener(ReferenceHolder<U> referenceHolder, T owner) {
        AbstractReferenceHolder.validationDisabled.set(true);
        try {
            referenceHolder.addListener(new ReverseRelationUpdater<T,U>(owner, reverse, referenceHolder));
        } finally {
            AbstractReferenceHolder.validationDisabled.set(false);
        }
    }

    public Reference<U> newReference(T owner) {
        Reference<U> reference = new ReferenceImpl<>(this, owner);
        addListener(reference, owner);
        return reference;
    }

    public References<U> newReferences(T owner) {
        References<U> references = new ReferencesImpl<>(this, owner);
        addListener(references, owner);
        return references;
    }
}
