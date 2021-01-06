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
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class ClassAnalyzer<T> extends ClassVisitor {
    private final Class<T> clazz;
    private final Map<BinaryRelation<T, ?>, Field> fieldMap;

    ClassAnalyzer(Class<T> clazz, Map<BinaryRelation<T, ?>, Field> fieldMap) {
        super(Opcodes.ASM8);
        this.clazz = clazz;
        this.fieldMap = fieldMap;
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.contentEquals("<init>")) {
            return new ConstructorAnalyzer<>(clazz, fieldMap);
        } else {
            return null;
        }
    }
}
