/*-
 * #%L
 * bidiref
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

public final class TransitiveRelation<T> extends BinaryRelationship<T,T,TransitiveReferences<T>,TransitiveReferences<T>,TransitiveRelation<T>,TransitiveRelation<T>> {
    private final Relation<T,T> association;
    private final TransitiveRelation<T> reverse;

    TransitiveRelation(Relation<T,T> association, TransitiveRelation<T> reverse) {
        this.association = association;
        this.reverse = reverse;
    }

    public TransitiveRelation(Relation<T,T> association) {
        this.association = association;
        reverse = new TransitiveRelation<T>(association.getReverse(), this);
    }

    public Relation<T,T> getAssociation() {
        return association;
    }

    @Override
    public TransitiveRelation<T> getReverse() {
        return reverse;
    }

    public TransitiveReferences<T> newTransitiveReferences(T owner) {
        return new TransitiveReferences<>(this, owner);
    }
}
