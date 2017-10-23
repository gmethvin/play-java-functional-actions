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

import java.util.concurrent.*;
import java.util.function.*;
import play.mvc.*;
import play.mvc.BodyParser;

/**
 * Helpers that can be mixed into a controller to provide functional action capabilities.
 */
public interface FunctionalControllerHelpers extends Http.Status, Http.HeaderNames {
    /**
     * @return the executor to use to execute the action body.
     */
    default Executor actionExecutor() {
        return play.core.Execution.trampoline();
    }

    /**
     * the default body parser configured for the current play application.
     *
     * This dependency can be satisfied by injecting a BodyParser.Default.
     *
     * @return the default body parser to use.
     */
    BodyParser<Object> defaultBodyParser();

    /**
     * @return an action returning a CompletionStage, using the default body parser
     */
    default FunctionalAction<Object> async(Function<Http.Request, CompletionStage<Result>> func) {
        return FunctionalAction.<Object>create(defaultBodyParser(), actionExecutor(), func);
    }

    /**
     * @return an action returning a Result directly, using the default body parser
     */
    default FunctionalAction<Object> action(Function<Http.Request, Result> func) {
        return FunctionalAction.<Object>create(defaultBodyParser(), actionExecutor(),
                req -> CompletableFuture.completedFuture(func.apply(req)));
    }

    /**
     * @return an action returning a CompletionStage, using the provided body parser.
     */
    default <A> FunctionalAction<A> async(
            BodyParser<A> parser, Function<Http.Request, CompletionStage<Result>> func) {
        return FunctionalAction.create(parser, actionExecutor(), func);
    }

    /**
     * @return an action returning a Result directly, using the provided body parser.
     */
    default <A> FunctionalAction<A> action(
            BodyParser<A> parser, Function<Http.Request, Result> func) {
        return FunctionalAction.create(parser, actionExecutor(),
                req -> CompletableFuture.completedFuture(func.apply(req)));
    }
}