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

package org.e2immu.kvstoreaapi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.e2immu.annotation.*;

import java.util.List;
import java.util.Map;
public class IoVertxCoreJson {
    public static final String PACKAGE_NAME = "io.vertx.core.json";

    @Container
    static class JsonArray$ {
        JsonArray$() { }

        @Modified
        @Fluent
        JsonArray add(Object value) { return null; }

        @NotNull
        @NotModified
        List getList() { return null; }
    }

    @Container
    static class JsonObject$ {
        JsonObject$() { }

        @NotModified @Nullable
        Object getValue(String key) { return null; }

        @Modified
        @Fluent
        JsonObject put(@NotNull String key, Object value) { return null; }

        @NotNull
        @NotModified
        String encode() { return null; }

        @NotNull
        @NotModified
        @Dependent
        Map<String, Object> getMap() { return null; }
    }
}
