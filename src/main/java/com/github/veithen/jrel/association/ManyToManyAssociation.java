/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 - 2022 Andreas Veithen
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

public final class ManyToManyAssociation<T1, T2>
        extends ToManyAssociation<T1, T2, MutableReferences<T1>, ManyToManyAssociation<T2, T1>> {
    private ManyToManyAssociation(
            Class<T1> type1,
            Class<T2> type2,
            ManyToManyAssociation<T2, T1> converse,
            Navigability navigability) {
        super(type1, type2, converse, navigability);
    }

    public ManyToManyAssociation(Class<T1> type1, Class<T2> type2, Navigability navigability) {
        this(type1, type2, null, navigability);
    }

    @Override
    protected ManyToManyAssociation<T2, T1> doCreateConverse() {
        return new ManyToManyAssociation<T2, T1>(getType2(), getType1(), this, navigability);
    }
}
