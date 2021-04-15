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
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.e2immu.annotation.*;

public class IoVertxExtWeb {
    public static final String PACKAGE_NAME = "io.vertx.ext.web";

    static class Route$ {
        @Fluent
        @Modified
        Route handler(@NotNull Handler<RoutingContext> handler) { return null; }
    }

    static class Router$ {
        @NotNull
        @NotModified
        static Router router(@Modified @NotNull Vertx vertx) { return null; }

        @NotNull
        @Modified
        Route route() { return null; }

        @NotNull
      //  @Modified FIXME causes infinite loop
        Route route(@NotNull HttpMethod httpMethod, @NotNull String string) { return null; }
    }

    @Container
    static class RoutingContext$ {
        @NotNull
        @NotModified
        HttpServerResponse response() { return null; }

        @NotModified @NotNull
        JsonObject getBodyAsJson() { return null; }

        @NotModified @NotNull
        JsonArray getBodyAsJsonArray() { return null; }

        @NotModified @Nullable
        String pathParam(String string) { return null; }
    }
}
