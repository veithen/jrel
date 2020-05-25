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

import java.util.function.Function;

import com.github.veithen.jrel.ReferenceHolder;
import com.github.veithen.jrel.ReferenceHolderCreationContext;

public abstract class ToOneAssociation<T1,T2,R2 extends ReferenceHolder<T1>> extends Association<T1,T2,MutableReference<T2>,R2> implements Function<T1,T2> {
    public ToOneAssociation(Class<T1> type) {
        super(type);
    }

    @Override
    protected final MutableReference<T2> doCreateReferenceHolder(ReferenceHolderCreationContext context) {
        return new MutableReference<>(context);
    }

    @Override
    public final T2 apply(T1 o) {
        return getReferenceHolder(o).get();
    }
}
