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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClassData<T> {
    private static final Map<Class<?>,ClassData<?>> instances = new HashMap<>();

    private final Class<T> clazz;
    private final List<BinaryRelation<T,?>> registeredRelations = new ArrayList<>();
    private Descriptor<T> descriptor;
    
    ClassData(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    static synchronized <T> ClassData<T> getInstance(Class<T> clazz) {
        return (ClassData<T>)instances.computeIfAbsent(clazz, k -> new ClassData<>(k));
    }

    synchronized void registerRelation(BinaryRelation<T,?> relation) {
        if (descriptor != null) {
            throw new IllegalStateException();
        }
        registeredRelations.add(relation);
    }
    
    synchronized Descriptor<T> getDescriptor() {
        if (descriptor == null) {
            descriptor = new Descriptor<>(clazz, registeredRelations);
        }
        return descriptor;
    }
}
