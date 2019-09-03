package com.gorgexec.rxjsonrpc.api.server.compiler;

import com.gorgexec.rxjsonrpc.api.compiler.CommonProcessor;
import com.gorgexec.rxjsonrpc.api.server.IJsonRpcServer;
import com.gorgexec.rxjsonrpc.api.server.JsonRpcServer;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ExecutableElement;

@SupportedAnnotationTypes("com.gorgexec.rxjsonrpc.api.server.JsonRpcServer")
public class Processor extends CommonProcessor {

    @Override
    protected Class<? extends Annotation> getMethodAnnotation() {
        return JsonRpcServer.class;
    }

    @Override
    protected String getFieldName() {
        return "server";
    }

    @Override
    protected Class<?> getFieldClass() {
        return IJsonRpcServer.class;
    }

    @Override
    protected void implementMethod(MethodSpec.Builder methodBuilder, ExecutableElement method, String methodName, TypeName responseType) {
        methodBuilder.addStatement("return server.incomingCalls($S,$L)", methodName, responseType.toString() + ".class");
    }

}
