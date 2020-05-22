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
package com.github.veithen.jrel.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a list of {@link SetListener} instances for use by {@link ListenableSet} instances.
 * 
 * @param <T> the type of elements in the set
 */
public final class SetListenerList<T> {
    private final List<SetListener<? super T>> listeners = new ArrayList<>();

    public void add(SetListener<? super T> listener) {
        listeners.add(listener);
    }

    public void remove(SetListener<? super T> listener) {
        listeners.remove(listener);
    }

    public void fireAdded(T object) {
        for (SetListener<? super T> listener : listeners) {
            listener.added(object);
        }
    }

    public void fireRemoved(T object) {
        for (SetListener<? super T> listener : listeners) {
            listener.removed(object);
        }
    }
}
