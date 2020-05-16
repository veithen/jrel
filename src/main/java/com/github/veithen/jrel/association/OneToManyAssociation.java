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

/**
 * A 1:N association. Note that this type of association can't be created directly. It exists as the
 * converse of a {@link ManyToOneAssociation}.
 */
public final class OneToManyAssociation<T,U> extends ToManyAssociation<T,U,Reference<T>> {
    private final ManyToOneAssociation<U,T> converse;

    OneToManyAssociation(ManyToOneAssociation<U,T> converse) {
        this.converse = converse;
    }

    public ManyToOneAssociation<U,T> getConverse() {
        return converse;
    }
}
