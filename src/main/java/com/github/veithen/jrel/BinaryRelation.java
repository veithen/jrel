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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.BiPredicate;

public abstract class BinaryRelation<T1,T2,R1 extends ReferenceHolder<T2>,R2 extends ReferenceHolder<T1>> implements BiPredicate<T1,T2> {
    private final Class<T1> type;
    private final Class<?> declaringClass;
    private String name;

    public BinaryRelation(Class<T1> type) {
        this.type = type;
        Class<?> declaringClass = null;
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (frame.getMethodName().equals("<clinit>")) {
                try {
                    declaringClass = type.getClassLoader().loadClass(frame.getClassName());
                } catch (ClassNotFoundException ex) {
                    // Just continue
                }
                break;
            }
        }
        this.declaringClass = declaringClass;
        Descriptor.registerRelation(type, this);
    }

    public final Class<T1> getType() {
        return type;
    }

    public synchronized final String getName() {
        if (name == null) {
            if (declaringClass != null) {
                for (Field field : declaringClass.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        try {
                            BinaryRelation<?,?,?,?> relation = (BinaryRelation<?,?,?,?>)field.get(null);
                            if (relation == this) {
                                name = declaringClass.getName() + "." + field.getName();
                                break;
                            } else if (relation.getConverse() == this) {
                                name = declaringClass.getName() + "." + field.getName() + "(^T)";
                            }
                        } catch (ClassCastException | IllegalAccessException ex) {
                            // Ignore and continue
                        }
                    }
                }
            }
            if (name == null) {
                name = "<anonymous>";
            }
        }
        return name;
    }

    @Override
    public final String toString() {
        return getName();
    }

    /**
     * Get the converse, i.e. the binary relation with both ends reversed.
     * 
     * @return the converse relation
     */
    public abstract BinaryRelation<T2,T1,R2,R1> getConverse();

    /**
     * If this is a derived relation, returns the binary relations used to compute the derived relation.
     * 
     * @return the dependencies or an empty array if this is not a derived relation
     */
    public abstract BinaryRelation<?,?,?,?>[] getDependencies();

    public final R1 newReferenceHolder(T1 owner) {
        ReferenceHolderSet referenceHolderSet = ReferenceHolderCreationContext.getReferenceHolderSet(owner);
        if (referenceHolderSet == null) {
            referenceHolderSet = Descriptor.getInstance(owner.getClass()).getReferenceHolderSetAccessor().get(owner);
        }
        if (referenceHolderSet == null) {
            referenceHolderSet = new ReferenceHolderSet();
        }
        ReferenceHolderCreationContext context = new ReferenceHolderCreationContext(this, owner, referenceHolderSet);
        context.push();
        try {
            return createReferenceHolder(context, owner);
        } finally {
            context.pop();
        }
    }

    protected abstract R1 createReferenceHolder(ReferenceHolderCreationContext context, T1 owner);

    @SuppressWarnings("unchecked")
    public final Optional<R1> getOptionalReferenceHolder(T1 owner) {
        ReferenceHolderAccessor accessor = Descriptor.getInstance(owner.getClass()).getReferenceHolderAccessor(this);
        return accessor == null ? Optional.empty() : Optional.of((R1)accessor.get(owner));
    }

    public final R1 getReferenceHolder(T1 owner) {
        return getOptionalReferenceHolder(owner).orElseThrow(() -> new IllegalStateException("Not bound"));
    }

    @Override
    public final boolean test(T1 o1, T2 o2) {
        return getReferenceHolder(o1).asSet().contains(o2);
    }
}
