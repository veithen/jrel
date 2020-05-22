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

import com.github.veithen.jrel.collection.ListenableSet;
import com.github.veithen.jrel.collection.SingletonIdentitySet;

final class ReferenceImpl<T,U> extends AbstractMutableReferenceHolder<T,U> implements Reference<U> {
    private final SingletonIdentitySet<U> set = new SingletonIdentitySet<>();

    ReferenceImpl(ToOneAssociation<T,U,?> association, T owner) {
        super(association, owner);
    }

    @Override
    public ListenableSet<U> asSet() {
        validate();
        return set;
    }

    public U get() {
        validate();
        return set.get();
    }

    public void set(U target) {
        validate();
        set.set(target);
    }
}
