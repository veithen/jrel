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

import java.util.function.BiPredicate;

import com.github.veithen.checkt.annotation.TypeToken;

public interface BinaryRelation<T1, T2> extends BiPredicate<T1, T2> {
    @TypeToken
    Class<T1> getType1();

    @TypeToken
    Class<T2> getType2();

    BinaryRelation<T2, T1> getConverse();

    ReferenceHolder<T2> newReferenceHolder(T1 owner);

    ReferenceHolder<T2> getReferenceHolder(T1 owner);
}
