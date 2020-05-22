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

public abstract class BinaryRelation<Type1,Type2,ReferenceHolder1 extends ReferenceHolder<Type2>,ReferenceHolder2 extends ReferenceHolder<Type1>> implements BiPredicate<Type1,Type2> {
    private Binding<Type1,ReferenceHolder1> binding;

    /**
     * Get the converse, i.e. the binary relation with both ends reversed.
     * 
     * @return the converse relation
     */
    public abstract BinaryRelation<Type2,Type1,ReferenceHolder2,ReferenceHolder1> getConverse();

    public final synchronized void bind(Binder<Type1,ReferenceHolder1> binder) {
        if (this.binding != null) {
            throw new IllegalStateException("Already bound");
        }
        this.binding = binder.createBinding(this::newReferenceHolder);
    }

    public final void bind(Binder<Type1,ReferenceHolder1> binder1, Binder<Type2,ReferenceHolder2> binder2) {
        bind(binder1);
        getConverse().bind(binder2);
    }

    public abstract ReferenceHolder1 newReferenceHolder(Type1 owner);

    public final synchronized ReferenceHolder1 getReferenceHolder(Type1 owner) {
        if (binding == null) {
            throw new IllegalStateException("Not bound");
        }
        return binding.getReferenceHolder(owner);
    }

    @Override
    public final boolean test(Type1 o1, Type2 o2) {
        return getReferenceHolder(o1).asSet().contains(o2);
    }
}
