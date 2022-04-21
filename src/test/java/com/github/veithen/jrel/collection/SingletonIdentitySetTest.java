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
package com.github.veithen.jrel.collection;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;

public class SingletonIdentitySetTest {
    interface Listener extends SetListener<Object> {}

    private static SingletonIdentitySet<Object> createTestSet() {
        SingletonIdentitySet<Object> set = new SingletonIdentitySet<>();
        ValidatingListener.addTo(set);
        return set;
    }

    @Test
    public void testSetUnset() {
        SingletonIdentitySet<Object> set = createTestSet();
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        Object object = new Object();
        set.set(object);
        verify(listener).added(object);
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object);
    }

    @Test
    public void testSetToSame() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object = new Object();
        set.set(object);
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        set.set(object);
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object);
    }

    @Test
    public void testSetAlreadySet() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        set.set(object1);
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        set.set(object2);
        verify(listener).removed(object1);
        verify(listener).added(object2);
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object2);
    }

    @Test
    public void testSetNull() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object = new Object();
        set.set(object);
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        set.set(null);
        verify(listener).removed(object);
        verifyNoMoreInteractions(listener);
        assertThat(set).isEmpty();
    }

    @Test
    public void testAddUnset() {
        SingletonIdentitySet<Object> set = createTestSet();
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        Object object = new Object();
        assertThat(set.add(object)).isTrue();
        verify(listener).added(object);
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object);
    }

    @Test
    public void testAddSame() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object = new Object();
        assertThat(set.add(object)).isTrue();
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        assertThat(set.add(object)).isFalse();
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object);
    }

    @Test
    public void testAddAlreadySet() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        assertThat(set.add(object1)).isTrue();
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        assertThat(set.add(object2)).isTrue();
        verify(listener).removed(object1);
        verify(listener).added(object2);
        verifyNoMoreInteractions(listener);
        assertThat(set).containsExactly(object2);
    }

    @Test
    public void testAddNull() {
        SingletonIdentitySet<Object> set = createTestSet();
        assertThrows(NullPointerException.class, () -> set.add(null));
    }

    @Test
    public void testRemove() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object = new Object();
        set.add(object);
        Listener listener = mock(Listener.class);
        set.addListener(listener);
        assertThat(set.remove(object)).isTrue();
        verify(listener).removed(object);
        verifyNoMoreInteractions(listener);
        assertThat(set).isEmpty();
    }

    @Test
    public void testRemoveNonExisting() {
        SingletonIdentitySet<Object> set = createTestSet();
        assertThat(set.remove(new Object())).isFalse();
    }

    @Test
    public void testRemoveNull() {
        SingletonIdentitySet<Object> set = createTestSet();
        assertThat(set.remove(null)).isFalse();
    }

    @Test
    public void testContains() {
        SingletonIdentitySet<Object> set = createTestSet();
        Object object = new Object();
        set.set(object);
        assertThat(set.contains(object)).isTrue();
        assertThat(set.contains(new Object())).isFalse();
    }

    @Test
    public void testContainsNull() {
        SingletonIdentitySet<Object> set = createTestSet();
        assertThat(set.contains(null)).isFalse();
    }

    @Test
    public void testSize() {
        SingletonIdentitySet<Object> set = createTestSet();
        assertThat(set.size()).isEqualTo(0);
        set.set(new Object());
        assertThat(set.size()).isEqualTo(1);
    }
}
