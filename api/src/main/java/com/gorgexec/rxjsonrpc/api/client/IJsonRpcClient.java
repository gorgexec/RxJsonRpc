package com.gorgexec.rxjsonrpc.api.client;

import java.util.Map;

import io.reactivex.Single;

public interface IJsonRpcClient {

    <T> Single<T> call(String method, Map<String, Object> params, Class<T> responseClass);
}
