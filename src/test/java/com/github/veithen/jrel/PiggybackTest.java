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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class PiggybackTest {
    @Test
    public void test() {
        Node node1 = new Node();
        Node node2 = new Node();
        node2.parent.set(node1);
        assertThat(node1.descendants).containsExactly(node2);
    }
}
