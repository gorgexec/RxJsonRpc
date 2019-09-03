package com.gorgexec.rxjsonrpc.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class GsonRpcResponseParser implements IJsonRpcResponseParser {

    private final Gson gson = new Gson();

    private String json;
    private Response response;

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public IJsonRpcResponseParser parse(String json) {
        this.json = json;
        this.response = gson.fromJson(json, Response.class);
        return this;
    }

    @Override
    public boolean isJsonRpcResponse() {
       return json.contains("\"jsonrpc\":\"2.0\"") && response.result != null || response.error !=null;
    }

    @Override
    public boolean hasError() {
        return response.error != null;
    }

    @Override
    public long responseId() {
        return response.id;
    }

    @Override
    public <T> T responseResult(Class<T> resultClass) {
        return gson.fromJson(response.result, resultClass);
    }

    @Override
    public Error error() {
        return response.error;
    }


    public static class Response {
        public long id;
        public JsonElement result;
        public Error error;

    }

}
