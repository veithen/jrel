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

import java.util.Objects;
import java.util.function.Function;

public final class Relation<T,U> {
    private final Relation<U,T> reverse;
    private Function<T,ReferenceHolder<U>> getter;

    private Relation(Relation<U,T> reverse) {
        this.reverse = reverse;
    }

    public Relation() {
        reverse = new Relation<U,T>(this);
    }

    public Relation<U,T> getReverse() {
        return reverse;
    }

    public synchronized void bind(Function<T,ReferenceHolder<U>> getter) {
        Objects.requireNonNull(getter);
        if (this.getter != null) {
            throw new IllegalStateException("Already bound");
        }
        this.getter = getter;
    }

    synchronized Function<T,ReferenceHolder<U>> getter() {
        if (getter == null) {
            throw new IllegalStateException("Not bound");
        }
        return getter;
    }

    public Reference<U> newReference(T owner) {
        return new Reference<U>(new ReverseRelationUpdater<T,U>(owner, reverse));
    }

    public References<U> newReferences(T owner) {
        return new References<U>(new ReverseRelationUpdater<T,U>(owner, reverse), 16, 0.5f);
    }
}
