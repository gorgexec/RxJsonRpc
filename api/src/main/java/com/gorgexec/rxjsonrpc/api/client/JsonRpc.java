package com.gorgexec.rxjsonrpc.api.client;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Documented
@Target({METHOD})
@Retention(SOURCE)
public @interface JsonRpc {
    String value() default "";
}
