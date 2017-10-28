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

import akka.NotUsed;
import akka.util.ByteString;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.*;
import play.mvc.BodyParser;

/**
 * Helpers that can be mixed into a controller to provide functional action capabilities.
 */
public interface FunctionalControllerHelpers {
    /**
     * @return the executor to use to execute the action body.
     */
    default Executor actionExecutor() {
        return play.core.Execution.trampoline();
    }

    /**
     * @return an action returning a CompletionStage that does not parse the body
     */
    default FAction<NotUsed> async(Supplier<CompletionStage<Result>> func) {
        return FAction.create(new EmptyBodyParser(), actionExecutor(), (req, body) -> func.get());
    }

    /**
     * @return an action returning a Result directly that does not parse the body
     */
    default FAction<NotUsed> action(Supplier<Result> func) {
        return FAction.create(new EmptyBodyParser(), actionExecutor(),
            (req, body) -> CompletableFuture.completedFuture(func.get()));
    }


    /**
     * @return an action returning a CompletionStage that does not parse the body
     */
    default FAction<NotUsed> async(Function<Http.RequestHeader, CompletionStage<Result>> func) {
        return FAction.create(new EmptyBodyParser(), actionExecutor(),
                (req, body) -> func.apply(req));
    }

    /**
     * @return an action returning a Result directly that does not parse the body
     */
    default FAction<NotUsed> action(Function<Http.RequestHeader, Result> func) {
        return FAction.create(new EmptyBodyParser(), actionExecutor(),
                (req, body) -> CompletableFuture.completedFuture(func.apply(req)));
    }

    /**
     * @return an action returning a CompletionStage, using the provided body parser.
     */
    default <B> FAction<B> async(
            BodyParser<B> parser, BiFunction<Http.RequestHeader, B, CompletionStage<Result>> func) {
        return FAction.create(parser, actionExecutor(), func);
    }

    /**
     * @return an action returning a Result directly, using the provided body parser.
     */
    default <B> FAction<B> action(
            BodyParser<B> parser, BiFunction<Http.RequestHeader, B, Result> func) {
        return FAction.create(parser, actionExecutor(),
                (req, body) -> CompletableFuture.completedFuture(func.apply(req, body)));
    }

    class EmptyBodyParser implements BodyParser<NotUsed> {
        @Override
        public Accumulator<ByteString, F.Either<Result, NotUsed>> apply(Http.RequestHeader request) {
            return Accumulator.done(F.Either.Right(NotUsed.getInstance()));
        }
    }
}
