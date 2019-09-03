package com.gorgexec.rxjsonrpc.server;

public interface IJsonRpcIncomingRequestParser {

    IJsonRpcIncomingRequestParser parse(String json);
    boolean isJsonRpcRequest();
    String method();
    <T> T methodParams(Class<T> paramsClass);

}
