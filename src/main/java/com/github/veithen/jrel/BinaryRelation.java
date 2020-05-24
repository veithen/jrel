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
import java.util.function.BiPredicate;

import org.objectweb.asm.ClassReader;

public abstract class BinaryRelation<T1,T2,R1 extends ReferenceHolder<T2>,R2 extends ReferenceHolder<T1>> implements BiPredicate<T1,T2> {
    private static final Map<Class<?>,Descriptor> descriptors = new HashMap<>();

    private synchronized Descriptor getDescriptor(Class<?> clazz) {
        Descriptor descriptor = descriptors.get(clazz);
        if (descriptor == null) {
            Class<?> superClass = clazz.getSuperclass();
            Descriptor parent = superClass == Object.class ? null : getDescriptor(superClass);
            Map<BinaryRelation<?,?,?,?>,Field> fieldMap = new HashMap<>();
            try (InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class")) {
                new ClassReader(in).accept(new ClassAnalyzer(clazz, fieldMap), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (IOException ex) {
                throw new AnalyzerException(ex);
            }
            descriptor = new Descriptor(parent, fieldMap);
            descriptors.put(clazz, descriptor);
        }
        return descriptor;
    }

    /**
     * Get the converse, i.e. the binary relation with both ends reversed.
     * 
     * @return the converse relation
     */
    public abstract BinaryRelation<T2,T1,R2,R1> getConverse();

    public abstract R1 newReferenceHolder(T1 owner);

    @SuppressWarnings("unchecked")
    public final R1 getReferenceHolder(T1 owner) {
        try {
            return (R1)getDescriptor(owner.getClass()).lookup(this).get(owner);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
    }

    @Override
    public final boolean test(T1 o1, T2 o2) {
        return getReferenceHolder(o1).asSet().contains(o2);
    }
}
