package org.e2immu.kvstoreaapi;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.e2immu.annotation.NotModified;
import org.e2immu.annotation.NotNull;
import org.e2immu.annotation.PropagateModification;

public class IoVertxConfig {
    public static final String PACKAGE_NAME = "io.vertx.config";

    static class ConfigRetriever$ {
        @NotNull
        ConfigRetriever create(@NotModified @NotNull Vertx vertx) { return null;  }

        @NotModified
        void getConfig(@NotNull @PropagateModification Handler<AsyncResult<JsonObject>> handler) { }
    }
}
