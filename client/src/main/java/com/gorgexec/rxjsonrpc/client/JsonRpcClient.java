package com.gorgexec.rxjsonrpc.client;

import com.gorgexec.rxjsonrpc.socket.IRxSocket;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class JsonRpcClient {

    private AtomicLong requestId = new AtomicLong(0);

    private final IRxSocket socket;
    private final IJsonRpcResponseParser parser;
    private final int responseTimeoutSeconds;

    public JsonRpcClient(IRxSocket socket, IJsonRpcResponseParser parser, int responseTimeoutSeconds) {
        this.socket = socket;
        this.parser = parser;
        this.responseTimeoutSeconds = responseTimeoutSeconds;
    }


    public <T> Single<T> call(String method, Map<String, Object> params, Class<T> responseClass) {

        long id = requestId.incrementAndGet();
        Request request = Request.create(id, method, params);

        return socket.sendMessage(parser.toJson(request))
                .flatMap(i -> captureResponse(request.id, responseClass));
    }

    private <T> Single<T> captureResponse(long requestId, Class<T> responseClass) {
        return socket.observeMessages()
                .observeOn(Schedulers.computation())
                .take(responseTimeoutSeconds, TimeUnit.SECONDS)
                .switchIfEmpty(Flowable.error(new JsonRpcCallTimeout()))
                .map(parser::parse)
                .filter(IJsonRpcResponseParser::isJsonRpcResponse)
                .filter(parser -> parser.responseId() == requestId)
                .firstOrError()
                .flatMap(parser -> parser.hasError()
                        ? Single.error(new JsonRpcCallException(parser.error().code, parser.error().message))
                        : Single.just(parser.responseResult(responseClass)));
    }

    public static class JsonRpcCallException extends Exception {

        public int code;
        public String message;

        public JsonRpcCallException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static class JsonRpcCallTimeout extends Exception {

        @Override
        public String getMessage() {
            return "JsonRpcCall response timeout";
        }
    }
}
