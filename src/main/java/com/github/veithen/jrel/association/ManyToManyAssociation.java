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

import com.github.veithen.jrel.DomainObject;

public final class ManyToManyAssociation<T1 extends DomainObject,T2 extends DomainObject> extends ToManyAssociation<T1,T2,MutableReferences<T1>> {
    private final ManyToManyAssociation<T2,T1> converse;

    ManyToManyAssociation(ManyToManyAssociation<T2,T1> converse) {
        this.converse = converse;
    }

    public ManyToManyAssociation() {
        converse = new ManyToManyAssociation<T2,T1>(this);
    }

    public ManyToManyAssociation<T2,T1> getConverse() {
        return converse;
    }
}
