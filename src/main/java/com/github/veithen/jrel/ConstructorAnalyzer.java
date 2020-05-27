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
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class ConstructorAnalyzer extends MethodVisitor {
    private enum State {
        NONE,
        THIS_LOADED,
        RELATION_LOADED,
        OWNER_LOADED,
        NEW_RELATION_HOLDER_INVOKED,
    }

    private final Class<?> clazz;
    private final Map<BinaryRelation<?,?>,Field> fieldMap;
    private State state = State.NONE;
    private @Nullable BinaryRelation<?,?> relation;

    ConstructorAnalyzer(Class<?> clazz, Map<BinaryRelation<?,?>,Field> fieldMap) {
        super(Opcodes.ASM8);
        this.clazz = clazz;
        this.fieldMap = fieldMap;
    }

    private void reset() {
        state = State.NONE;
        relation = null;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (opcode == Opcodes.ALOAD && var == 0) {
            switch (state) {
                case NONE:
                    state = State.THIS_LOADED;
                    return;
                case RELATION_LOADED:
                    state = State.OWNER_LOADED;
                    return;
                default:
            }
        }
        reset();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (state == State.THIS_LOADED && opcode == Opcodes.GETSTATIC) {
            Object fieldValue;
            try {
                Field field = ReflectionUtil.getClassLoader(clazz).loadClass(owner.replace('/', '.')).getDeclaredField(name);
                field.setAccessible(true);
                fieldValue = ReflectionUtil.getStaticFieldValue(field);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
                throw new AnalyzerException(ex);
            }
            if (fieldValue instanceof BinaryRelation) {
                state = State.RELATION_LOADED;
                relation = (BinaryRelation<?,?>)fieldValue;
                return;
            }
        } else if (state == State.NEW_RELATION_HOLDER_INVOKED && opcode == Opcodes.PUTFIELD) {
            Field field;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                throw new AnalyzerException(ex);
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                throw new AnalyzerException("Field " + name + " in " + clazz.getName() + " is not final");
            }
            field.setAccessible(true);
            assert relation != null;
            if (fieldMap.containsKey(relation)) {
                throw new AnalyzerException("Relation is already bound");
            }
            fieldMap.put(relation, field);
        }
        reset();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor,
            boolean isInterface) {
        if (state == State.RELATION_LOADED && opcode == Opcodes.INVOKEVIRTUAL && name.equals("getConverse")) {
            assert relation != null;
            relation = relation.getConverse();
            return;
        } else if (state == State.OWNER_LOADED && opcode == Opcodes.INVOKEVIRTUAL && name.equals("newReferenceHolder")) {
            state = State.NEW_RELATION_HOLDER_INVOKED;
            return;
        }
        reset();
    }
}
