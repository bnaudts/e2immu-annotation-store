package org.e2immu.kvstoreaapi;

import org.e2immu.annotation.NotModified;
import org.e2immu.annotation.NotNull;

import java.util.logging.Logger;

public class JavaUtilLogging {

    public static final String PACKAGE_NAME = "java.util.logging";

    static class Logger$ {
        @NotNull
        static Logger getLogger(String name) { return null; }

        @NotModified
        void severe(String msg) { }

        @NotModified
        void info(String msg) { }

        @NotModified
        void fine(String msg) { }
    }
}
