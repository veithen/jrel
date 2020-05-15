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

import java.util.ArrayList;
import java.util.List;

class ListenableCollectionSupport<T> {
    private final List<CollectionListener<? super T>> listeners = new ArrayList<>();

    public final void addListener(CollectionListener<? super T> listener) {
        listeners.add(listener);
    }

    final void fireAdded(T object) {
        for (CollectionListener<? super T> listener : listeners) {
            listener.added(object);
        }
    }

    final void fireRemoved(T object) {
        for (CollectionListener<? super T> listener : listeners) {
            listener.removed(object);
        }
    }
}