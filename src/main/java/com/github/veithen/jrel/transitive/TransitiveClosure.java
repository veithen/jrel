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

import java.util.Iterator;

import com.github.veithen.jrel.BinaryRelation;
import com.github.veithen.jrel.ReferenceHolder;

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

    /**
     * Apply transitive reduction. Let <i>R</i> be the binary relation from which this transitive
     * closure (denoted by <i>R<sup>+</sup></i>) is constructed. This will remove <i>(x,&nbsp;y)</i>
     * from <i>R</i> if there is a <i>z&nbsp;&#8800;&nbsp;y</i> such that <i>x&nbsp;R&nbsp;z</i> and
     * <i>z&nbsp;R<sup>+</sup>&nbsp;y</i>.
     * 
     * @param x
     */
    public void reduce(T x) {
        ReferenceHolder<T> refs = relation.getReferenceHolder(x);
        for (Iterator<T> it = refs.asSet().iterator(); it.hasNext(); ) {
            T y = it.next();
            for (T z : refs.asSet()) {
                if (z != y && getReferenceHolder(z).contains(y)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
