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
package com.github.veithen.jrel;

import java.util.function.BiPredicate;

public abstract class BinaryRelation<T1 extends DomainObject,T2 extends DomainObject,R1 extends ReferenceHolder<T2>,R2 extends ReferenceHolder<T1>> implements BiPredicate<T1,T2> {
    /**
     * Get the converse, i.e. the binary relation with both ends reversed.
     * 
     * @return the converse relation
     */
    public abstract BinaryRelation<T2,T1,R2,R1> getConverse();

    protected abstract R1 newReferenceHolder(T1 owner);

    public final R1 getReferenceHolder(T1 owner) {
        return owner.getDomain().getBinaryRelationInstance(this).getReferenceHolder(owner);
    }

    @Override
    public final boolean test(T1 o1, T2 o2) {
        return getReferenceHolder(o1).asSet().contains(o2);
    }
}
