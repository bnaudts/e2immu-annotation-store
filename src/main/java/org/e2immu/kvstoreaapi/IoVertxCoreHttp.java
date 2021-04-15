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

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.*;
import org.e2immu.annotation.*;

public class IoVertxCoreHttp {
    public static final String PACKAGE_NAME = "io.vertx.core.http";

    static class HttpServer$ {
        @Fluent
        //@Modified // FIXME causes infinite loop
        HttpServer requestHandler(@NotNull Handler<HttpServerRequest> handler) { return null; }

        @NotNull
        Future<HttpServer> listen(int i) { return null; }
    }

    @Container
    static class HttpServerResponse$ {
        @Fluent
        @Modified
        HttpServerResponse setStatusCode(int i) { return null; }

        @Fluent
        @Modified
        HttpServerResponse putHeader(@NotNull String string, String string1) { return null; }
        Future<Void> end(String string) { return null; }
    }

    @E2Container
    static class HttpMethod$ {
        @NotNull
        static final HttpMethod GET = null;
        @NotNull
        static final HttpMethod POST = null;
        @NotNull
        static final HttpMethod PUT = null;
    }
}
