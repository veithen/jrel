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
package com.github.veithen.jrel.transitive;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

public class TransitiveClosureTest {
    @Test
    public void test1() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        Node node5 = new Node("5");
        Node node6 = new Node("6");
        node2.parent.set(node1);
        node3.parent.set(node2);
        assertThat(node1.descendants).containsExactly(node2, node3);
        assertThat(node3.ancestors).containsExactly(node1, node2);
        node4.parent.set(node1);
        node5.parent.set(node2);
        assertThat(node1.descendants).containsExactly(node2, node3, node4, node5);
        node6.parent.set(node4);
        assertThat(node1.descendants).containsExactly(node2, node3, node4, node5, node6);
        node2.children.clear();
        assertThat(node1.descendants).containsExactly(node2, node4, node6);
    }

    @Test
    public void test2() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        node3.parent.set(node2);
        node4.parent.set(node2);
        assertThat(node1.descendants).isEmpty();
        node2.parent.set(node1);
        assertThat(node1.descendants).containsExactly(node2, node3, node4);
        node4.parent.clear();
        assertThat(node1.descendants).containsExactly(node2, node3);
    }

    @Test
    public void test3() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        node2.parent.set(node1);
        node3.parent.set(node2);
        assertThat(node1.descendants).containsExactly(node2, node3);
        node2.parent.clear();
        assertThat(node1.descendants).isEmpty();
        node4.parent.set(node2);
        assertThat(node1.descendants).isEmpty();
    }

    @Test
    public void testAdd() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Node("1").descendants.add(new Node("2")));
    }

    @Test
    public void testAddAll() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Node("1").descendants.addAll(Arrays.asList(new Node("2"))));
    }

    @Test
    public void testRemove() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Node("1").descendants.remove(new Node("2")));
    }

    @Test
    public void testRemoveAll() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Node("1").descendants.removeAll(Arrays.asList(new Node("2"))));
    }

    @Test
    public void testRetainAll() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Node("1").descendants.retainAll(Arrays.asList(new Node("2"))));
    }

    @Test
    public void testClear() {
        assertThrows(
                UnsupportedOperationException.class,
                new Node("1").descendants::clear);
    }

    @Test
    public void testIteratorRemove() {
        Node node1 = new Node("1");
        node1.children.add(new Node("2"));
        Iterator<Node> it = node1.descendants.iterator();
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }
}
