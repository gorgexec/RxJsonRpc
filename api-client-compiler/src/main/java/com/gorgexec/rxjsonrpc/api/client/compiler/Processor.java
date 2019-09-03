package com.gorgexec.rxjsonrpc.api.client.compiler;

import com.gorgexec.rxjsonrpc.api.compiler.CommonProcessor;
import com.gorgexec.rxjsonrpc.api.client.IJsonRpcClient;
import com.gorgexec.rxjsonrpc.api.client.JsonRpc;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

@SupportedAnnotationTypes("com.gorgexec.rxjsonrpc.api.client.JsonRpc")
public class Processor extends CommonProcessor {

    @Override
    protected Class<? extends Annotation> getMethodAnnotation() {
        return JsonRpc.class;
    }

    @Override
    protected String getFieldName() {
        return "client";
    }

    @Override
    protected Class<?> getFieldClass() {
        return IJsonRpcClient.class;
    }

    @Override
    protected void implementMethod(MethodSpec.Builder methodBuilder, ExecutableElement method, String methodName, TypeName responseType) {

        Map<String, TypeName> params = new LinkedHashMap<>();
        for (VariableElement variableElement : method.getParameters()) {
            params.put(variableElement.getSimpleName().toString(), ParameterizedTypeName.get(variableElement.asType()));
        }

        methodBuilder.addStatement("$T<String, Object> params = new $T<>()", Map.class, HashMap.class);

        for (Map.Entry<String, TypeName> p : params.entrySet()) {
            methodBuilder.addParameter(p.getValue(), p.getKey());
            methodBuilder.addStatement("params.put($S, $N)", p.getKey(), p.getKey());
        }

        methodBuilder.addStatement("return client.call($S,$N,$L)", methodName, "params", responseType.toString() + ".class");
    }

}
