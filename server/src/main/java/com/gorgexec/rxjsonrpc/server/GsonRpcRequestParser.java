package com.gorgexec.rxjsonrpc.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class GsonRpcRequestParser implements IJsonRpcIncomingRequestParser {

    private final Gson gson = new Gson();
    private Request request;

    @Override
    public GsonRpcRequestParser parse(String json) {
        this.request = gson.fromJson(json, Request.class);
        return this;
    }

    @Override
    public boolean isJsonRpcRequest() {
        return request.method != null;
    }

    @Override
    public String method() {
        return request.method;
    }

    @Override
    public <T> T methodParams(Class<T> paramsClass) {
        return gson.fromJson(request.params, paramsClass);
    }

    public static class Request {
        public long id;
        public String method;
        public JsonElement params;

    }
}
