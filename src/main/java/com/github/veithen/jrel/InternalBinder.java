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

import java.util.function.Function;

public final class InternalBinder<T,R extends ReferenceHolder<?>> implements Binder<T,R>, Binding<T,R> {
    private Function<T,R> referenceHolderFunction;

    public InternalBinder(Function<T,R> referenceHolderFunction) {
        this.referenceHolderFunction = referenceHolderFunction;
    }

    @Override
    public Binding<T,R> createBinding(ReferenceHolderFactory<T,R> referenceHolderFactory) {
        return this;
    }

    @Override
    public R getReferenceHolder(T owner) {
        return referenceHolderFunction.apply(owner);
    }
}
