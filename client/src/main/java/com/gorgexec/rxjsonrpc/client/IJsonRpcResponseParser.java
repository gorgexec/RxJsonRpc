package com.gorgexec.rxjsonrpc.client;

public interface IJsonRpcResponseParser {
    String toJson(Object object);
    IJsonRpcResponseParser parse(String json);
    boolean isJsonRpcResponse();
    boolean hasError();
    long responseId();
    <T> T responseResult(Class<T> responseClass);
    Error error();
}
