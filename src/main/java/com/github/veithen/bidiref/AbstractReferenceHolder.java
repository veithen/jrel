/*-
 * #%L
 * bidiref
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
package com.github.veithen.bidiref;

public abstract class AbstractReferenceHolder<T,U> implements ReferenceHolder<U> {
    private final Relation<T,U> relation;
    private final T owner;
    private boolean validated;

    AbstractReferenceHolder(Relation<T, U> relation, T owner) {
        this.relation = relation;
        this.owner = owner;
    }

    final void validate() {
        if (!validated) {
            if (relation.getter().apply(owner) != this) {
                throw new IllegalStateException();
            }
            validated = true;
        }
    }
}
