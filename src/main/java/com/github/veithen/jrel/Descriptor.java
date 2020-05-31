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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;

final class Descriptor<T> {
    private final Map<BinaryRelation<? super T,?>,ReferenceHolderAccessor> accessorMap = new HashMap<>();
    private final ReferenceHolderSetAccessor referenceHolderSetAccessor;

    Descriptor(Class<T> clazz, List<BinaryRelation<T,?>> registeredRelations) {
        Class<? super T> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            accessorMap.putAll(ClassData.getInstance(superClass).getDescriptor().accessorMap);
        }
        Map<BinaryRelation<T,?>,Field> fieldMap = new LinkedHashMap<>();
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class")) {
            new ClassReader(in).accept(new ClassAnalyzer<>(clazz, fieldMap), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (IOException ex) {
            throw new AnalyzerException(ex);
        }
        for (Map.Entry<BinaryRelation<T,?>,Field> entry : fieldMap.entrySet()) {
            accessorMap.put(entry.getKey(), new BoundReferenceHolderAccessor(entry.getKey(), entry.getValue()));
        }
        List<BoundReferenceHolderAccessor> boundReferenceHolderAccessors = new ArrayList<>();
        for (ReferenceHolderAccessor accessor : accessorMap.values()) {
            if (accessor instanceof BoundReferenceHolderAccessor) {
                boundReferenceHolderAccessors.add((BoundReferenceHolderAccessor)accessor);
            }
        }
        if (boundReferenceHolderAccessors.isEmpty()) {
            referenceHolderSetAccessor = null;
        } else {
            referenceHolderSetAccessor = new ReferenceHolderSetAccessor(boundReferenceHolderAccessors);
            for (BinaryRelation<T,?> relation : registeredRelations) {
                if (!accessorMap.containsKey(relation)) {
                    accessorMap.put(relation, new UnboundReferenceHolderAccessor(referenceHolderSetAccessor, relation));
                }
            }
        }
    }

    ReferenceHolderAccessor getReferenceHolderAccessor(BinaryRelation<?,?> relation) {
        return accessorMap.get(relation);
    }

    ReferenceHolderSetAccessor getReferenceHolderSetAccessor() {
        return referenceHolderSetAccessor;
    }
}
