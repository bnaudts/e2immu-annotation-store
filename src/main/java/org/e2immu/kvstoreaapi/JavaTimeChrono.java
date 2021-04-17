package org.e2immu.kvstoreaapi;
import org.e2immu.annotation.E2Container;
import org.e2immu.annotation.NotNull;

import java.time.Instant;
import java.time.ZoneOffset;
public class JavaTimeChrono {
    public static final String PACKAGE_NAME = "java.time.chrono";

    @E2Container
    static class ChronoLocalDateTime$ {
        @NotNull
        Instant toInstant(@NotNull ZoneOffset offset) { return null; }
    }
}
