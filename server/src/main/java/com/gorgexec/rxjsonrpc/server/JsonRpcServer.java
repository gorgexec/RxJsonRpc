package com.gorgexec.rxjsonrpc.server;


import com.gorgexec.rxjsonrpc.socket.IRxSocket;

import io.reactivex.Flowable;

public class JsonRpcServer {

    private IRxSocket socket;
    private IJsonRpcIncomingRequestParser parser;

    public JsonRpcServer(IRxSocket socket, IJsonRpcIncomingRequestParser parser) {
        this.socket = socket;
        this.parser = parser;
    }

    public <T> Flowable<T> incomingCalls(String method, Class<T> callClass) {
        return socket.observeMessages()
                .map(parser::parse)
                .filter(IJsonRpcIncomingRequestParser::isJsonRpcRequest)
                .filter(parser->parser.method().equals(method))
                .map(request -> parser.methodParams(callClass));
    }

}
