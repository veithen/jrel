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
package com.github.veithen.bidiref;

public abstract class AbstractMutableReferenceHolder<T,U> implements MutableReferenceHolder<U> {
    static final ThreadLocal<Boolean> validationDisabled = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private final Association<T,U> association;
    private final T owner;
    private boolean validated;

    AbstractMutableReferenceHolder(Association<T, U> association, T owner) {
        this.association = association;
        this.owner = owner;
    }

    final void validate() {
        if (!validated && !validationDisabled.get()) {
            if (association.getReferenceHolder(owner) != this) {
                throw new IllegalStateException();
            }
            validated = true;
        }
    }
}
