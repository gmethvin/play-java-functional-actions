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

import akka.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import play.mvc.*;
import play.libs.streams.*;

public abstract class FunctionalAction<A> extends EssentialAction {

    /**
     *
     * @param request the request
     * @return the future result of this action after parsing the body and running the action function
     */
    public abstract CompletionStage<Result> apply(Http.Request request);

    /**
     * @return the BodyParser used by this action
     */
    public abstract BodyParser<A> bodyParser();

    /**
     * @return the Executor used to execute the action body
     */
    public abstract Executor executor();

    /**
     * Apply this action to a raw request header.
     *
     * @param requestHeader the request header to apply to
     * @return the result as a sink represented by an Accumulator
     */
    @Override
    public final Accumulator<ByteString, Result> apply(Http.RequestHeader requestHeader) {
        return bodyParser().apply(requestHeader).mapFuture(either -> {
            if (either.left.isPresent()) {
                Result result = either.left.get();
                return CompletableFuture.completedFuture(result);
            } else {
                A body = either.right.get();
                Http.Request request = new Http.RequestImpl(requestHeader.asScala().withBody(body));
                return apply(request);
            }
        }, executor());
    }

    /**
     * Convenience method for creating a FunctionalAction from a function
     */
    public static <A> FunctionalAction<A> create(
        BodyParser<A> parser,
        Executor executor,
        Function<Http.Request, CompletionStage<Result>> func
    ) {
        return new FunctionalAction<A>() {
            @Override
            public CompletionStage<Result> apply(Http.Request request) {
                return func.apply(request);
            }

            @Override
            public BodyParser<A> bodyParser() {
                return parser;
            }

            @Override
            public Executor executor() {
                return executor;
            }
        };
    }
}
