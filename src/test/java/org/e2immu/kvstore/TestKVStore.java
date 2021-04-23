/*
 * e2immu: a static code analyser for effective and eventual immutability
 * Copyright 2020-2021, Bart Naudts, https://www.e2immu.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.e2immu.kvstore;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestKVStore {
    private static final Logger LOGGER = Logger.getLogger(TestKVStore.class.getCanonicalName());

    private static final String LOCALHOST = "localhost";
    private static final String PROJECT = "p1";
    private static final String JAVA_UTIL_SET = "java.util.Set";
    private static final String JAVA_UTIL_MAP = "java.util.Map";
    private static final String ORG_E2IMMU_KVSTORE_STORE_READ_WITHIN_MILLIS = "org.e2immu.kvstore.Store:readWithinMillis";

    private static final String CONTAINER = "container";
    private static final String E2IMMU = "e2immu";

    @BeforeAll
    public static void beforeClass(Vertx vertx) {
        new Store(vertx);
    }

    @AfterAll
    public static void afterClass(Vertx vertx) {
        vertx.close();
    }

    @Test
    public void test_01_putOneKey(Vertx vertx, VertxTestContext testContext) {
        WebClient webClient = WebClient.create(vertx);
        String requestURI = Store.API_VERSION + "/set/" + PROJECT + "/" + JAVA_UTIL_SET + "/" + CONTAINER;
        webClient.get(Store.DEFAULT_PORT, LOCALHOST, requestURI)
                .send(ar -> {
                    if (ar.failed()) {
                        LOGGER.severe("Failure: " + ar.cause().getMessage());
                        ar.cause().printStackTrace();
                    } else {
                        assertTrue(ar.succeeded());
                        try {
                            JsonObject result = ar.result().bodyAsJsonObject();
                            if (ar.result().statusCode() != 200) {
                                LOGGER.severe("ERROR: " + result);
                            }

                            assertEquals(200, ar.result().statusCode());
                            LOGGER.info("Got update summary: " + result);
                            int updated = result.getInteger("updated");
                            assertEquals(1, updated);
                        } catch (DecodeException decodeException) {
                            LOGGER.severe("Received: " + ar.result().bodyAsString());
                        }
                        testContext.completeNow();
                    }
                });
    }

    @Test
    public void test_02_getOneKey(Vertx vertx, VertxTestContext testContext) {
        WebClient webClient = WebClient.create(vertx);
        webClient.get(Store.DEFAULT_PORT, LOCALHOST, Store.API_VERSION + "/get/" + PROJECT + "/" + JAVA_UTIL_SET)
                .send(ar -> {
                    if (ar.failed()) {
                        LOGGER.severe("Failure: " + ar.cause().getMessage());
                        ar.cause().printStackTrace();
                        fail();
                    } else {
                        assertTrue(ar.succeeded());
                        JsonObject result = ar.result().bodyAsJsonObject();
                        if (ar.result().statusCode() != 200) {
                            LOGGER.severe("ERROR: " + result);
                        }
                        assertEquals(200, ar.result().statusCode());
                        LOGGER.info("Got value for java.util.set: " + result);
                        String value = result.getString(JAVA_UTIL_SET);
                        assertEquals(CONTAINER, value);
                    }
                    testContext.completeNow();
                });
    }

    @Test
    public void test_03_putMultipleKeys(Vertx vertx, VertxTestContext vertxTestContext) {
        WebClient webClient = WebClient.create(vertx);
        JsonObject putObject = new JsonObject()
                .put(JAVA_UTIL_SET, "new value")
                .put(JAVA_UTIL_MAP, CONTAINER)
                .put(ORG_E2IMMU_KVSTORE_STORE_READ_WITHIN_MILLIS, E2IMMU);
        Buffer body = Buffer.buffer(putObject.encode());
        webClient.put(Store.DEFAULT_PORT, LOCALHOST, Store.API_VERSION + "/set/" + PROJECT)
                .sendBuffer(body, ar -> {
                    if (ar.failed()) {
                        LOGGER.severe("Failure: " + ar.cause().getMessage());
                        ar.cause().printStackTrace();
                        fail();
                    } else {
                        assertTrue(ar.succeeded());
                        assertEquals(200, ar.result().statusCode());

                        JsonObject result = ar.result().bodyAsJsonObject();
                        LOGGER.info("Got update summary: " + result);
                        int updated = result.getInteger("updated");
                        int ignored = result.getInteger("ignored");
                        int removed = result.getInteger("removed");
                        assertEquals(3, updated);
                        assertEquals(0, ignored);
                        assertEquals(0, removed);

                    }
                    vertxTestContext.completeNow();
                });
    }

    @Test
    public void test_04_getOneKey_Expect2(Vertx vertx, VertxTestContext vertxTestContext) {
        WebClient webClient = WebClient.create(vertx);
        webClient.get(Store.DEFAULT_PORT, LOCALHOST, Store.API_VERSION + "/get/" + PROJECT + "/" + JAVA_UTIL_MAP)
                .send(ar -> {
                    if (ar.failed()) {
                        LOGGER.severe("Failure: " + ar.cause().getMessage());
                        ar.cause().printStackTrace();
                        fail();
                    } else {
                        assertTrue(ar.succeeded());
                        JsonObject result = ar.result().bodyAsJsonObject();
                        if (ar.result().statusCode() != 200) {
                            LOGGER.severe("ERROR: " + result);
                        }
                        assertEquals(200, ar.result().statusCode());
                        LOGGER.info("Got values: " + result);
                        assertEquals(2, result.size());
                        String set = result.getString(JAVA_UTIL_SET);
                        assertEquals("new value", set);
                        String map = result.getString(JAVA_UTIL_MAP);
                        assertEquals(CONTAINER, map);
                    }
                    vertxTestContext.completeNow();
                });
    }
}
