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
package com.github.veithen.jrel.composition;

import com.github.veithen.jrel.BinaryRelation;
import com.github.veithen.jrel.References;

public final class CompositionRelation<T1,T2,T3> extends BinaryRelation<T1,T3,References<T3>,References<T1>>{
    private final BinaryRelation<T1,T2,?,?> relation1;
    private final BinaryRelation<T2,T3,?,?> relation2;
    private final CompositionRelation<T3,T2,T1> converse;

    CompositionRelation(BinaryRelation<T1,T2,?,?> relation1, BinaryRelation<T2,T3,?,?> relation2, CompositionRelation<T3,T2,T1> converse) {
        this.relation1 = relation1;
        this.relation2 = relation2;
        this.converse = converse;
    }

    public CompositionRelation(BinaryRelation<T1,T2,?,?> relation1, BinaryRelation<T2,T3,?,?> relation2) {
        this.relation1 = relation1;
        this.relation2 = relation2;
        converse = new CompositionRelation<T3,T2,T1>(relation2.getConverse(), relation1.getConverse(), this);
    }

    public BinaryRelation<T1,T2,?,?> getRelation1() {
        return relation1;
    }

    public BinaryRelation<T2,T3,?,?> getRelation2() {
        return relation2;
    }

    public CompositionRelation<T3,T2,T1> getConverse() {
        return converse;
    }

    @Override
    public BinaryRelation<?,?,?,?>[] getDependencies() {
        return new BinaryRelation<?,?,?,?>[] { relation1, relation2 };
    }

    @Override
    public References<T3> newReferenceHolder(T1 owner) {
        return new CompositeReferences<>(this, owner);
    }
}
