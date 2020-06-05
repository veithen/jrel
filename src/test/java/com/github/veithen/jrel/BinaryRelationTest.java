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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class BinaryRelationTest {
    @Test
    public void testGetName() {
        assertThat(Node.PARENT.getName()).isEqualTo("com.github.veithen.jrel.Node.PARENT");
        assertThat(Node.PARENT.getConverse().getName()).isEqualTo("com.github.veithen.jrel.Node.PARENT(^T)");
    }
}
