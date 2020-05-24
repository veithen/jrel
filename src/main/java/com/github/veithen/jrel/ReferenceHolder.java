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

import com.github.veithen.jrel.collection.ListenableSet;

/**
 * A reference holder.
 * <p>
 * For a given relation <i>R</i> and a given <i>x</i>, the reference holder represents the set of
 * all <i>y</i> for which <i>x&nbsp;R&nbsp;y</i>. <i>x</i> is called the <i>owner</i> and the
 * reference holder for a given owner is returned by
 * {@link BinaryRelation#getReferenceHolder(Object)}. By definition, the content of a reference
 * holder can always be represented as a set, which is why the interface defines an {@link #asSet()}
 * method. Some specific relations are functions and the size of that set is at most one.
 * Subinterfaces provide more convenient APIs for that case and the more general case where the set
 * can have more than one element.
 * 
 * @param <T> the type of reference stored by this holder
 */
public interface ReferenceHolder<T> {
    ListenableSet<T> asSet();
}
