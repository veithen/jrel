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

import com.github.veithen.jrel.Domain;

public class TransitiveClosureTest {
    @Test
    public void test1() {
        Domain domain = new Domain();
        TreeNode node1 = new TreeNode(domain, "1");
        TreeNode node2 = new TreeNode(domain, "2");
        TreeNode node3 = new TreeNode(domain, "3");
        TreeNode node4 = new TreeNode(domain, "4");
        TreeNode node5 = new TreeNode(domain, "5");
        TreeNode node6 = new TreeNode(domain, "6");
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
        Domain domain = new Domain();
        TreeNode node1 = new TreeNode(domain, "1");
        TreeNode node2 = new TreeNode(domain, "2");
        TreeNode node3 = new TreeNode(domain, "3");
        TreeNode node4 = new TreeNode(domain, "4");
        node3.parent.set(node2);
        node4.parent.set(node2);
        assertThat(node1.descendants).isEmpty();
        node2.parent.set(node1);
        assertThat(node1.descendants).containsExactly(node2, node3, node4);
        node4.parent.set(null);
        assertThat(node1.descendants).containsExactly(node2, node3);
    }

    @Test
    public void test3() {
        Domain domain = new Domain();
        TreeNode node1 = new TreeNode(domain, "1");
        TreeNode node2 = new TreeNode(domain, "2");
        TreeNode node3 = new TreeNode(domain, "3");
        TreeNode node4 = new TreeNode(domain, "4");
        node2.parent.set(node1);
        node3.parent.set(node2);
        assertThat(node1.descendants).containsExactly(node2, node3);
        node2.parent.set(null);
        assertThat(node1.descendants).isEmpty();
        node4.parent.set(node2);
        assertThat(node1.descendants).isEmpty();
    }

    @Test
    public void testAdd() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().add(new TreeNode(domain, "2")));
    }

    @Test
    public void testAddAll() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().addAll(Arrays.asList(new TreeNode(domain, "2"))));
    }

    @Test
    public void testRemove() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().remove(new TreeNode(domain, "2")));
    }

    @Test
    public void testRemoveAll() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().removeAll(Arrays.asList(new TreeNode(domain, "2"))));
    }

    @Test
    public void testRetainAll() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().retainAll(Arrays.asList(new TreeNode(domain, "2"))));
    }

    @Test
    public void testClear() {
        Domain domain = new Domain();
        assertThrows(
                UnsupportedOperationException.class,
                () -> new TreeNode(domain, "1").descendants.asSet().clear());
    }

    @Test
    public void testIteratorRemove() {
        Domain domain = new Domain();
        TreeNode node1 = new TreeNode(domain, "1");
        node1.children.add(new TreeNode(domain, "2"));
        Iterator<TreeNode> it = node1.descendants.iterator();
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    @Test
    public void testReduce() {
        Domain domain = new Domain();
        GraphNode node1 = new GraphNode(domain, "1");
        GraphNode node2 = new GraphNode(domain, "1");
        GraphNode node3 = new GraphNode(domain, "1");
        node2.parents.add(node1);
        node3.parents.add(node1);
        node3.parents.add(node2);
        GraphNode.ANCESTOR.reduce(node3);
        assertThat(node3.parents).containsExactly(node2);
    }
}
