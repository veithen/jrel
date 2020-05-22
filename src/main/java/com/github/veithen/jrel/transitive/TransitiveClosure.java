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

import com.github.veithen.jrel.BinaryRelation;

public final class TransitiveClosure<T> extends BinaryRelation<T,T,TransitiveReferences<T>,TransitiveReferences<T>> {
    private final BinaryRelation<T,T,?,?> relation;
    private final TransitiveClosure<T> converse;

    TransitiveClosure(BinaryRelation<T,T,?,?> relation, TransitiveClosure<T> converse) {
        this.relation = relation;
        this.converse = converse;
    }

    public TransitiveClosure(BinaryRelation<T,T,?,?> relation) {
        this.relation = relation;
        converse = new TransitiveClosure<T>(relation.getConverse(), this);
    }

    /**
     * Get the binary relation from which this transitive closure was constructed.
     * 
     * @return
     */
    public BinaryRelation<T,T,?,?> getRelation() {
        return relation;
    }

    @Override
    public TransitiveClosure<T> getConverse() {
        return converse;
    }

    public TransitiveReferences<T> newReferenceHolder(T owner) {
        return new TransitiveReferences<>(this, owner);
    }
}
