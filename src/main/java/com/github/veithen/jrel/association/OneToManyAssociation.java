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

public final class OneToManyAssociation<T1, T2>
        extends ToManyAssociation<T1, T2, MutableReference<T1>, ManyToOneAssociation<T2, T1>> {
    OneToManyAssociation(Class<T1> type1, Class<T2> type2, ManyToOneAssociation<T2, T1> converse) {
        // One-to-many associations must be bidirectional so that the multiplicity can be enforced.
        super(type1, type2, converse, Navigability.BIDIRECTIONAL);
    }

    public OneToManyAssociation(Class<T1> type1, Class<T2> type2) {
        this(type1, type2, null);
    }

    protected ManyToOneAssociation<T2, T1> doCreateConverse() {
        return new ManyToOneAssociation<T2, T1>(getType2(), getType1(), this, navigability);
    }
}
