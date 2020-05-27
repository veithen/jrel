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

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractBinaryRelation<T1,T2,R1 extends ReferenceHolder<T2>,R2 extends ReferenceHolder<T1>,C extends AbstractBinaryRelation<T2,T1,R2,R1,?>> implements BinaryRelation<T1,T2> {
    private final Class<T1> type1;
    private final Class<T2> type2;
    private final @Nullable Class<?> declaringClass;
    private @Nullable String name;
    private C converse;

    public AbstractBinaryRelation(Class<T1> type1, Class<T2> type2, C converse) {
        this.type1 = type1;
        this.type2 = type2;
        this.converse = converse;
        Class<?> declaringClass = null;
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (frame.getMethodName().equals("<clinit>")) {
                try {
                    declaringClass = ReflectionUtil.getClassLoader(type1).loadClass(frame.getClassName());
                } catch (ClassNotFoundException ex) {
                    // Just continue
                }
                break;
            }
        }
        this.declaringClass = declaringClass;
        Descriptor.registerRelation(type1, this);
    }

    public final Class<T1> getType1() {
        return type1;
    }

    public final Class<T2> getType2() {
        return type2;
    }

    public synchronized final String getName() {
        if (name == null) {
            if (declaringClass != null) {
                for (Field field : declaringClass.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        try {
                            BinaryRelation<?,?> relation = (BinaryRelation<?,?>)ReflectionUtil.getStaticFieldValue(field);
                            if (relation == this) {
                                name = declaringClass.getName() + "." + field.getName();
                                break;
                            }
                        } catch (ClassCastException | IllegalAccessException ex) {
                            // Ignore and continue
                        }
                    }
                }
            }
            if (name == null) {
                if (converse != null) {
                    name = converse.getName() + "(^T)";
                } else {
                    name = "<anonymous>";
                }
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
    public final synchronized C getConverse() {
        if (converse == null) {
            converse = createConverse();
        }
        return converse;
    }

    protected abstract C createConverse();

    /**
     * If this is a derived relation, returns the binary relations used to compute the derived relation.
     * 
     * @return the dependencies or an empty array if this is not a derived relation
     */
    public abstract BinaryRelation<?,?>[] getDependencies();

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
            return createReferenceHolder(owner);
        } finally {
            context.pop();
        }
    }

    protected abstract R1 createReferenceHolder(T1 owner);

    @SuppressWarnings("unchecked")
    public final R1 getReferenceHolder(T1 owner) {
        ReferenceHolderAccessor accessor = Descriptor.getInstance(owner.getClass()).getReferenceHolderAccessor(this);
        if (accessor == null) {
            throw new IllegalStateException("Not bound");
        }
        return (R1)accessor.get(owner);
    }

    @Override
    public final boolean test(T1 o1, T2 o2) {
        return getReferenceHolder(o1).asSet().contains(o2);
    }
}
