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
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;

final class Descriptor {
    private static final Map<Class<?>,Descriptor> instances = new HashMap<>();

    private final Descriptor parent;
    private final Map<BinaryRelation<?,?,?,?>,ReferenceHolderAccessor> accessorMap = new HashMap<>();

    private Descriptor(Descriptor parent, Map<BinaryRelation<?,?,?,?>,Field> fieldMap) {
        this.parent = parent;
        for (Map.Entry<BinaryRelation<?,?,?,?>,Field> entry : fieldMap.entrySet()) {
            accessorMap.put(entry.getKey(), new FieldAccessor(entry.getKey(), entry.getValue()));
        }
        outer: while (true) {
            for (Map.Entry<BinaryRelation<?,?,?,?>,ReferenceHolderAccessor> entry : accessorMap.entrySet()) {
                boolean modified = false;
                // TODO: this is not entirely correct because the dependencies may be relations between unrelated types
                for (BinaryRelation<?,?,?,?> dependency : entry.getKey().getDependencies()) {
                    if (getReferenceHolderAccessor(dependency) == null) {
                        accessorMap.put(dependency, new PiggybackAccessor(entry.getValue(), dependency));
                        modified = true;
                    }
                }
                if (modified) {
                    continue outer;
                }
            }
            break;
        }
    }

    static synchronized Descriptor getInstance(Class<?> clazz) {
        Descriptor descriptor = instances.get(clazz);
        if (descriptor == null) {
            Class<?> superClass = clazz.getSuperclass();
            Descriptor parent = superClass == Object.class ? null : getInstance(superClass);
            Map<BinaryRelation<?,?,?,?>,Field> fieldMap = new HashMap<>();
            try (InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class")) {
                new ClassReader(in).accept(new ClassAnalyzer(clazz, fieldMap), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (IOException ex) {
                throw new AnalyzerException(ex);
            }
            descriptor = new Descriptor(parent, fieldMap);
            instances.put(clazz, descriptor);
        }
        return descriptor;
    }

    ReferenceHolderAccessor getReferenceHolderAccessor(BinaryRelation<?,?,?,?> relation) {
        ReferenceHolderAccessor accessor = accessorMap.get(relation);
        if (accessor != null) {
            return accessor;
        }
        if (parent == null) {
            return null;
        }
        return parent.getReferenceHolderAccessor(relation);
    }
}
