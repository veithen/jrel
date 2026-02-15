/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 - 2022 Andreas Veithen
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

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class ReflectionUtil {
    private ReflectionUtil() {}

    static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) {
            throw new IllegalStateException("Cannot get class loader for class " + clazz.getName());
        }
        return classLoader;
    }

    static @Nullable Object getStaticFieldValue(Field field) throws IllegalAccessException {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException(field + " is not static");
        }
        return field.get(null);
    }
}
