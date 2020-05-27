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
package com.github.veithen.jrel.association;

import com.github.veithen.jrel.AbstractBinaryRelation;
import com.github.veithen.jrel.BinaryRelation;
import com.github.veithen.jrel.ReferenceHolder;

public abstract class Association<T1,T2,R1 extends ReferenceHolder<T2>,R2 extends ReferenceHolder<T1>,C extends Association<T2,T1,R2,R1,?>> extends AbstractBinaryRelation<T1,T2,R1,R2,C> {
    protected final Navigability navigability;

    public Association(Class<T1> type1, Class<T2> type2, C converse, Navigability navigability) {
        super(type1, type2, converse);
        this.navigability = navigability;
    }

    @Override
    protected final C createConverse() {
        if (navigability != Navigability.BIDIRECTIONAL) {
            throw new UnsupportedOperationException("Association " + this + " is not bidirectional");
        }
        return doCreateConverse();
    }

    protected abstract C doCreateConverse();

    @Override
    public final BinaryRelation<?,?>[] getDependencies() {
        return new BinaryRelation<?,?>[0];
    }

    @Override
    protected final R1 createReferenceHolder(T1 owner) {
        R1 referenceHolder = doCreateReferenceHolder();
        if (navigability == Navigability.BIDIRECTIONAL) {
            referenceHolder.asSet().addListener(new ConverseAssociationUpdater<T1,T2>(owner, getConverse(), referenceHolder));
        }
        return referenceHolder;
    }

    protected abstract R1 doCreateReferenceHolder();
}
