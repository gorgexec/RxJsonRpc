package com.gorgexec.rxjsonrpc.socket;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface IRxSocket {
    Single<Boolean> sendMessage(String message);
    Flowable<String> observeMessages();
}
