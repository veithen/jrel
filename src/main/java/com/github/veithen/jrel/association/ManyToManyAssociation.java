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

public final class ManyToManyAssociation<T,U> extends ToManyAssociation<T,U,References<T>> {
    private final ManyToManyAssociation<U,T> converse;

    ManyToManyAssociation(ManyToManyAssociation<U,T> converse) {
        this.converse = converse;
    }

    public ManyToManyAssociation() {
        converse = new ManyToManyAssociation<U,T>(this);
    }

    public ManyToManyAssociation<U,T> getConverse() {
        return converse;
    }
}
