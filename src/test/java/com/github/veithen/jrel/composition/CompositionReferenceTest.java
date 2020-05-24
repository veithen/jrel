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
package com.github.veithen.jrel.composition;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class CompositionReferenceTest {
    @Test
    public void test() {
        A a = new A();
        B b1 = new B();
        B b2 = new B();
        C c1 = new C();
        C c2 = new C();
        C c3 = new C();
        assertThat(a.c).isEmpty();
        b1.c.add(c1);
        a.b.add(b1);
        assertThat(a.c).containsExactly(c1);
        b1.c.add(c2);
        assertThat(a.c).containsExactly(c1, c2);
        a.b.add(b2);
        b2.c.add(c3);
        assertThat(a.c).containsExactly(c1, c2, c3);
        b1.c.add(c3);
        assertThat(a.c).containsExactly(c1, c2, c3);
        b2.c.remove(c3);
        assertThat(a.c).containsExactly(c1, c2, c3);
        b1.c.remove(c3);
        assertThat(a.c).containsExactly(c1, c2);
    }
}
