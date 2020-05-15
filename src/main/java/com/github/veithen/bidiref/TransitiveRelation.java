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

public final class TransitiveRelation<T> extends Relation<T,T> {
    private Function<T,TransitiveReferences<T>> transitiveGetter;

    public TransitiveRelation() {
        super();
    }

    TransitiveRelation(TransitiveRelation<T> reverse) {
        super(reverse);
    }

    @Override
    TransitiveRelation<T> createReverse() {
        return new TransitiveRelation<T>(this);
    }

    @Override
    public TransitiveRelation<T> getReverse() {
        return (TransitiveRelation<T>)super.getReverse();
    }

    public synchronized void bindTransitive(Function<T,TransitiveReferences<T>> transitiveGetter) {
        Objects.requireNonNull(transitiveGetter);
        if (this.transitiveGetter != null) {
            throw new IllegalStateException("Already bound");
        }
        this.transitiveGetter = transitiveGetter;
    }

    synchronized TransitiveReferences<T> getTransitiveReferences(T owner) {
        if (transitiveGetter == null) {
            throw new IllegalStateException("Not bound");
        }
        return transitiveGetter.apply(owner);
    }

    public TransitiveReferences<T> newTransitiveReferences(T owner) {
        return new TransitiveReferences<>(this, owner);
    }
}
