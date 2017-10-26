/*
 * Copyright 2017 Greg Methvin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package play.javadsl.functional;

import org.junit.Test;
import play.Application;
import play.api.routing.Router;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithServer;

import javax.inject.Singleton;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class FunctionalControllerIntegrationTest extends WithServer {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(Router.class).toProvider(TestRoutesProvider.class).in(Singleton.class))
                .build();
    }

    @Test
    public void testControllerGet() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        final String body = contentAsString(result);
        assertThat(body, containsString("Hello World"));
    }

    @Test
    public void testControllerPostJson() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/echo")
                .bodyJson(Json.parse("{ \"foo\": 10 }"));

        Result result = route(app, request);
        final String body = contentAsString(result);
        assertThat(body, containsString("{\"foo\":10}"));
    }
}