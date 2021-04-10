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

import org.e2immu.analyser.config.Configuration;
import org.e2immu.analyser.config.DebugConfiguration;
import org.e2immu.analyser.config.InputConfiguration;
import org.e2immu.analyser.output.Formatter;
import org.e2immu.analyser.output.FormattingOptions;
import org.e2immu.analyser.output.OutputBuilder;
import org.e2immu.analyser.parser.Input;
import org.e2immu.analyser.parser.Parser;
import org.e2immu.analyser.resolver.SortedType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.e2immu.analyser.util.Logger.LogTarget.*;

public class TestAnalyser {

    /*
    Test that directly runs the analyser -- useful for debugging in the Vertx context.
    I'd rather not have the dependencies in the e2immu/e2immu/analyser project.
     */

    @Test
    public void test() throws IOException {
        DebugConfiguration debugConfiguration = new DebugConfiguration.Builder().build();
        InputConfiguration.Builder inputConfigurationBuilder = new InputConfiguration.Builder()
                .addSources("src/main/java")
                .addClassPath(InputConfiguration.DEFAULT_CLASSPATH)
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "org/slf4j")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "org/junit/jupiter/api")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "ch/qos/logback/core/spi")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/core/") // trailing / is important
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/config")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/ext/web/common") // catch common
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/ext/web/impl")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/ext/bridge")
                .addClassPath(Input.JAR_WITH_PATH_PREFIX + "io/vertx/junit5");
        Configuration configuration = new Configuration.Builder()
                .setDebugConfiguration(debugConfiguration)
                .addDebugLogTargets(List.of(ANALYSER, INSPECT, BYTECODE_INSPECTOR).stream()
                        .map(Enum::toString).collect(Collectors.joining(",")))
                .setInputConfiguration(inputConfigurationBuilder.build())
                .build();
        configuration.initializeLoggers();
        Parser parser = new Parser(configuration);
        List<SortedType> types = parser.run();
        for (SortedType sortedType : types) {
            OutputBuilder outputBuilder = sortedType.primaryType().output();
            Formatter formatter = new Formatter(FormattingOptions.DEFAULT);
            System.out.println(formatter.write(outputBuilder));
        }
    }
}
