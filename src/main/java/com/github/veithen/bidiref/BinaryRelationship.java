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

import java.util.Objects;
import java.util.function.Function;

public abstract class BinaryRelationship<Type1,Type2,ReferenceHolder1 extends ReferenceHolder<Type2>,ReferenceHolder2 extends ReferenceHolder<Type1>,Self extends BinaryRelationship<Type1,Type2,ReferenceHolder1,ReferenceHolder2,Self,Opposite>,Opposite extends BinaryRelationship<Type2,Type1,ReferenceHolder2,ReferenceHolder1,Opposite,Self>> {
    private Function<Type1,ReferenceHolder1> getter;

    public abstract Opposite getReverse();

    public final synchronized void bind(Function<Type1,ReferenceHolder1> getter) {
        Objects.requireNonNull(getter);
        if (this.getter != null) {
            throw new IllegalStateException("Already bound");
        }
        this.getter = getter;
    }

    final synchronized ReferenceHolder1 getReferenceHolder(Type1 owner) {
        if (getter == null) {
            throw new IllegalStateException("Not bound");
        }
        return getter.apply(owner);
    }
}
