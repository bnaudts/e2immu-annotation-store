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

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.*;
import java.util.logging.Logger;

public class Store {
    public static final String API_VERSION = "/v1";

    private static final Logger LOGGER = Logger.getLogger(Store.class.getCanonicalName());
    public static final int DEFAULT_PORT = 8281;
    public static final long DEFAULT_READ_WITHIN_MILLIS = 5000L;

    private long readWithinMillis;
    private final Vertx vertx;
    private final Map<String, Project> projects = new HashMap<>();

    public Store(Vertx vertx) {
        this.vertx = vertx;
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
     /*   retriever.getConfig(ar -> {
            if (ar.failed()) {
                // Failed to retrieve the configuration
                LOGGER.severe("Cannot retrieve configuration");
                System.exit(1);
            } else {
                JsonObject config = ar.result();
                int port = (int) flexible(config.getValue("e2immu-port"), DEFAULT_PORT);
                this.readWithinMillis = flexible(config.getValue("e2immu-read-within-millis"), DEFAULT_READ_WITHIN_MILLIS);
                initServer(port);
            }
        });*/
    }
/*
    private static long flexible(Object object, long defaultValue) {
        LOGGER.info("Parsing " + object);
        if (object == null) return defaultValue;
        String s = object.toString();
        try {
            double d = Double.parseDouble(s);
            return (long) d;
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
*/
    private void initServer(int port) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
/*
        router.route(HttpMethod.GET, API_VERSION + "/get/:project/:element").handler(rc -> {
            String project = rc.pathParam("project");
            String element = rc.pathParam("element");
            handleGet(rc, project, element);
        });
        router.route(HttpMethod.GET, API_VERSION + "/set/:project/:element/:annotation").handler(rc -> {
            String project = rc.pathParam("project");
            String element = rc.pathParam("element");
            String annotation = rc.pathParam("annotation");
            handleSet(rc, project, element, annotation);
        });*/
        router.route(HttpMethod.POST, API_VERSION + "/get/:project").handler(rc -> {
            String project = rc.pathParam("project");
            JsonArray body = rc.getBodyAsJsonArray();
            handleMultiGet(rc, project, body);
        });/*
        router.route(HttpMethod.PUT, API_VERSION + "/set/:project").handler(rc -> {
            String project = rc.pathParam("project");
            JsonObject body = rc.getBodyAsJson();
            handleMultiSet(rc, project, body);
        });
        router.route(HttpMethod.GET, API_VERSION + "/list/:project").handler(rc -> {
            String project = rc.pathParam("project");
            handleListProject(rc, project);
        });
        router.route(HttpMethod.GET, API_VERSION + "/list").handler(this::handleListProjects);
**/
        server.requestHandler(router).listen(port);
        LOGGER.info("Started kv server on port " + port + "; read-within-millis " + readWithinMillis);
    }

    private Project getOrCreate(String projectName) {
        Project inMap = projects.get(projectName);
        if (inMap != null) {
            return inMap;
        }
        Project newProject = new Project(projectName);
        projects.put(projectName, newProject);
        LOGGER.info("Created new project " + projectName);
        return newProject;
    }
/*
    private static void ok(RoutingContext rc, JsonObject jsonObject) {
        List<String> list = rc.queryParam("nice");
        boolean nice = !list.isEmpty();
        rc.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(nice ? jsonObject.encodePrettily() : jsonObject.encode());
    }

    private static void badRequest(RoutingContext rc, String message) {
        rc.response()
                .putHeader("content-type", "text/plain")
                .setStatusCode(400)
                .end(message);
    }

    private void handleListProjects(RoutingContext rc) {
        JsonArray result = new JsonArray();
        projects.keySet().forEach(result::add);
        ok(rc, new JsonObject().put("projects", result));
    }

    private void handleListProject(RoutingContext rc, String projectName) {
        Project inMap = projects.get(projectName);
        if (inMap == null) {
            badRequest(rc, "Unknown project " + projectName);
            return;
        }
        JsonObject result = new JsonObject();
        inMap.visit(result::put);
        ok(rc, result);
    }

    private void handleSet(RoutingContext rc, String projectName, String element, String annotation) {
        handleMultiSet(rc, projectName, new JsonObject().put(element, annotation));
    }

    private void handleMultiSet(RoutingContext rc, String projectName, JsonObject body) {
        Project project = getOrCreate(projectName);
        int countUpdated = 0;
        int countIgnored = 0;
        int countRemoved = 0;
        if (body == null || body.getMap() == null) {
            badRequest(rc, "Body is empty");
            return;
        }
        try {
            for (Map.Entry<String, Object> entry : body.getMap().entrySet()) {
                String element = entry.getKey();
                if (entry.getValue() instanceof String) {
                    String current = (String) entry.getValue();
                    if (current.isEmpty()) {
                        String prev = project.remove(element);
                        if (prev != null) countRemoved++;
                    } else {
                        String prev = project.set(element, current);
                        if (prev == null || !prev.equals(current)) countUpdated++;
                    }
                } else {
                    countIgnored++;
                }
            }
        } catch (RuntimeException re) {
            re.printStackTrace();
            LOGGER.severe(re.getMessage());
        }
        LOGGER.info("Multi-set updated " + countUpdated + " ignored " + countIgnored + " removed " + countRemoved);
        ok(rc, new JsonObject()
                .put("updated", countUpdated)
                .put("ignored", countIgnored)
                .put("removed", countRemoved));
    }

    private void handleGet(RoutingContext rc, String projectName, String element) {
        handleMultiGet(rc, projectName, new JsonArray().add(element));
    }
*/
    private void handleMultiGet(RoutingContext rc, String projectName, JsonArray body) {
        Project project = getOrCreate(projectName);
  /*      JsonObject result = new JsonObject();
        Set<String> queried = new HashSet<>();
        for (Object element : body.getList()) {
            if (element instanceof String) {
                String key = (String) element;
                queried.add(key);
                String annotation = project.get(key);
                result.put(key, annotation != null ? annotation : "");
            }
        }
        Map<String, String> recent = project.recentlyReadAndUpdatedAfterwards(queried, readWithinMillis);
        for (Map.Entry<String, String> entry : recent.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        ok(rc, result);*/
    }
/*
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        new Store(vertx);
    }*/
}
