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

package play.javadsl.functional.controllers;

import javax.inject.Inject;

import akka.NotUsed;
import com.fasterxml.jackson.databind.JsonNode;
import play.api.cache.Cached;
import play.api.mvc.EssentialAction;
import play.javadsl.functional.FAction;
import play.javadsl.functional.FunctionalController;
import play.mvc.BodyParser;

public class TestController extends FunctionalController {

    private BodyParser.Json jsonParser;
    private Cached cached;

    @Inject
    public TestController(BodyParser.Json jsonParser, Cached cached) {
        this.jsonParser = jsonParser;
        this.cached = cached;
    }

    public FAction<NotUsed> index() {
        return action(() -> ok("Hello World"));
    }

    public FAction<JsonNode> echo() {
        return action(jsonParser, (req, jsonNode) ->
            ok(jsonNode).withHeaders("Foo", "Bar")
        );
    }

    // An example of how we can use the Scala helpers on Java functional actions
    public EssentialAction getItem(String id) {
        // we could probably create an interface with a helper method for this
        return cached.apply(id).apply(action(req ->
                ok("you got " + id)
        ));
    }

}
