package com.gorgexec.rxjsonrpc.api.server;

import io.reactivex.Flowable;

public interface IJsonRpcServer {

   <T> Flowable<T> incomingCalls(String method, Class<T> callClass);

}
