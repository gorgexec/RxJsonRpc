package com.gorgexec.rxjsonrpc.client;

import java.util.Map;

public class Request {
    public long id;
    public String method;
    public Map<String, Object> params;


    private Request() {}

    public static Request create(long id, String method, Map<String, Object> params){
        Request res = new Request();
        res.id = id;
        res.method = method;
        res.params = params;
        return res;
    }
}
