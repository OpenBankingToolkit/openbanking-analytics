/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.configuration.discovery;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ControllerEndpointBlacklistTest {

    private Method foo1;
    private Method foo2;
    private ControllerEndpointBlacklist controllerEndpointBlacklist;

    @Before
    public void setUp() throws Exception {
        foo1 = TestClass.class.getMethod("foo1");
        foo2 = TestClass.class.getMethod("foo2");
        controllerEndpointBlacklist = new ControllerEndpointBlacklist();
    }

    @Test
    public void addMethodToBlacklistAndContains() throws Exception{
        // When
        controllerEndpointBlacklist.add(TestClass.class, foo2);

        // then
        assertThat(controllerEndpointBlacklist.contains(TestClass.class, foo1)).isFalse();
        assertThat(controllerEndpointBlacklist.contains(TestClass.class, foo2)).isTrue();
        assertThat(controllerEndpointBlacklist.contains(TestClass.class, null)).isFalse();
        assertThat(controllerEndpointBlacklist.contains(null, foo2)).isFalse();
    }

    public static final class TestClass {
        public void foo1() {
        }

        public void foo2() {
        }
    }
}