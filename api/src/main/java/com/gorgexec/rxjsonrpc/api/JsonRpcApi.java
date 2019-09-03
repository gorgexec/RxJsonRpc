package com.gorgexec.rxjsonrpc.api;

import com.gorgexec.rxjsonrpc.api.compiler.CommonProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class JsonRpcApi {

    private JsonRpcApi() {}

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T create(Object clientOrServer, Class<T> apiClass) {

        T res = null;

        try {
            Class<?> implClass = Class.forName(apiClass.getCanonicalName()+ CommonProcessor.IMPLEMENTATION_SUFFIX);

            Constructor constructor = null;
            for(Constructor c: implClass.getDeclaredConstructors())
                if (c.getModifiers() == Modifier.PUBLIC) {
                    constructor = c;
                    break;
                }
            constructor.setAccessible(true);
            res = (T)constructor.newInstance(clientOrServer);
        } catch (Exception e) {
            String d = "";
        }


        return res;
    }
}
