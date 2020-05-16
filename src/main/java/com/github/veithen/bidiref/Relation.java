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

public final class Relation<T,U> extends BinaryRelation<T,U,MutableReferenceHolder<U>,MutableReferenceHolder<T>,Relation<T,U>,Relation<U,T>> {
    private final Relation<U,T> converse;

    Relation(Relation<U,T> converse) {
        this.converse = converse;
    }

    public Relation() {
        converse = new Relation<U,T>(this);
    }

    public Relation<U,T> getConverse() {
        return converse;
    }

    private void addListener(MutableReferenceHolder<U> referenceHolder, T owner) {
        AbstractMutableReferenceHolder.validationDisabled.set(true);
        try {
            referenceHolder.addListener(new ConverseRelationUpdater<T,U>(owner, converse, referenceHolder));
        } finally {
            AbstractMutableReferenceHolder.validationDisabled.set(false);
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
