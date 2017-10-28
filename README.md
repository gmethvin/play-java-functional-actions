# Play Java functional actions

This library is a basic proof of concept to show how to create functional-style actions in Play using the Java `EssentialAction` API.

## Background

By default, Play Java actions are represented as methods that accept a request and return a `CompletionStage<Result>` or a `Result`. Play can't use that directly, though. Instead it needs to convert that action to an `EssentialAction`. In that process we also need to compose the user-provided action with any actions specified by the annotations on each method.

Currently, Java action functionality in Play is actually provided by some Scala magic: we have a [`HandlerInvokerFactory` for Java actions](https://github.com/playframework/playframework/blob/2.6.6/framework/src/play/src/main/scala/play/core/routing/HandlerInvoker.scala#L133), and the router generated by the routes compiler requires an implicit `HandlerInvokerFactory` for the method result type.

Using `EssentialAction` for Java actions directly is much more straightforward to understand, and allows us to use the same action composition logic as with Scala Actions. We still need to provide a Java API for it to feel idiomatic, but it is much easier to wrap the Scala API with a functional Java API than to implement the current Java `Action` using annotations.

## Example

The current implementation uses a `FunctionalController` that provides an `action` and an `async` method for methods returning `Result` and `CompletionStage<Result>` respectively. By default, if a body parser is not passed as the first argument, an empty body parser is used, which returns `akka.NotUsed`.

For example:

```java
public class TestController extends FunctionalController {

    private BodyParser.Json jsonParser;

    @Inject
    public TestController(BodyParser.Json jsonParser) {
        this.jsonParser = jsonParser;
    }

    public FAction<NotUsed> index() {
        return action(() -> ok("Hello World"));
    }

    public FAction<JsonNode> echo() {
        return action(jsonParser, (req, jsonNode) ->
            ok(jsonNode).withHeaders("Foo", "Bar")
        );
    }
}
```

The `Http.Context` is intentionally not accessible within these actions, and the methods that use it like `request()`, `session()`, etc. are not provided. The idea is that we should provide alternate functional APIs that don't require state from a `ThreadLocal`.
