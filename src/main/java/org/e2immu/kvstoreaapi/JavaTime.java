package org.e2immu.kvstoreaapi;
import org.e2immu.annotation.E2Container;
import org.e2immu.annotation.NotModified;
import org.e2immu.annotation.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
public class JavaTime {
    public static final String PACKAGE_NAME = "java.time";

    static class LocalDateTime$ {
        @NotNull
        static LocalDateTime now() { return null; }
    }

    @E2Container
    static class Instant$ {
        @NotNull
        Instant plusMillis(long millisToAdd) { return null; }
        boolean isAfter(Instant otherInstant) { return false; }
        boolean isBefore(Instant otherInstant) { return false; }
    }

    static class ZoneOffset$ {
        @NotNull
        static final ZoneOffset UTC = null;
    }
}
