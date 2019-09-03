package com.gorgexec.rxjsonrpc.api.compiler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public abstract class CommonProcessor extends AbstractProcessor {

    static final String IMPLEMENTATION_SUFFIX = "_Impl";

    private Class<? extends Annotation> methodAnnotation;
    private String fieldName;
    private Class<?> fieldClass;

    private Map<TypeElement, Set<ExecutableElement>> interfaceMethods = new HashMap<>();


    private Messager messager;
    private Filer filer;


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        methodAnnotation = getMethodAnnotation();
        fieldName = getFieldName();
        fieldClass = getFieldClass();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(methodAnnotation)) {

            if (element.getKind() == ElementKind.METHOD) {
                Element enclosingElement = element.getEnclosingElement();
                if (enclosingElement.getKind() == ElementKind.INTERFACE) {
                    TypeElement apiInterface = (TypeElement) enclosingElement;
                    ExecutableElement executableElement = (ExecutableElement) element;

                    Set<ExecutableElement> methods = interfaceMethods.get(apiInterface);
                    if (methods == null) {
                        methods = new HashSet<>();
                        interfaceMethods.put(apiInterface, methods);
                    }

                    methods.add(executableElement);

                }
            }
        }

        for (Map.Entry<TypeElement, Set<ExecutableElement>> item : interfaceMethods.entrySet()) {
            createImplementation(item.getKey(), item.getValue());
        }

        return true;
    }

    protected void createImplementation(TypeElement apiInterface, Set<ExecutableElement> methods) {

        String interfaceName = apiInterface.getSimpleName().toString();
        String packageName = getPackageName(apiInterface);

        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(interfaceName + IMPLEMENTATION_SUFFIX)
                .addSuperinterface(ParameterizedTypeName.get(apiInterface.asType()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        FieldSpec field = FieldSpec.builder(fieldClass, fieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        classBuilder.addField(field);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldClass, fieldName)
                .addStatement("this.$N = $N", fieldName, fieldName)
                .build();

        classBuilder.addMethod(constructor);

        for (ExecutableElement method : methods) {

            String methodName = method.getSimpleName().toString();
            ParameterizedTypeName returnType = (ParameterizedTypeName) ParameterizedTypeName.get(method.getReturnType());
            TypeName responseType = returnType.typeArguments.get(0);

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType);

            implementMethod(methodBuilder, method, methodName, responseType);

            classBuilder.addMethod(methodBuilder.build());
        }

        try {
            JavaFile.builder(packageName, classBuilder.build())
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract Class<? extends Annotation> getMethodAnnotation();

    protected abstract String getFieldName();

    protected abstract Class<?> getFieldClass();

    protected abstract void implementMethod(MethodSpec.Builder methodBuilder, ExecutableElement method, String methodName, TypeName responseType);

    protected static String getPackageName(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement.getKind() == ElementKind.PACKAGE) {
            return ((PackageElement) enclosingElement).getQualifiedName().toString();
        } else {
            return getPackageName(enclosingElement);
        }
    }
}
