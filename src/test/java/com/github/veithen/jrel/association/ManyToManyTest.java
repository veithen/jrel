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
package com.github.veithen.jrel.association;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class ManyToManyTest {
    @Test
    public void test() {
        Interface i1 = new Interface();
        Interface i2 = new Interface();
        Interface i3 = new Interface();
        Interface i4 = new Interface();
        Interface i5 = new Interface();
        i3.superInterfaces.add(i1);
        i3.superInterfaces.add(i2);
        i4.superInterfaces.add(i3);
        i5.superInterfaces.add(i3);
        assertThat(i1.childInterfaces).containsExactly(i3);
        assertThat(i2.childInterfaces).containsExactly(i3);
        assertThat(i3.childInterfaces).containsExactly(i4, i5);
    }
}
