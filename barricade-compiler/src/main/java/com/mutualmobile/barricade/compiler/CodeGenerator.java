package com.mutualmobile.barricade.compiler;

import com.mutualmobile.barricade.IBarricadeConfig;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.JavaFile.builder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Generates code for a Barricade configuration.
 */
final class CodeGenerator {
  private static final String CLASS_NAME = "BarricadeConfig";
  private static final String ENDPOINTS_CLASS_NAME = "Endpoints";
  private static final String RESPONSES_CLASS_NAME = "Responses";
  private static final String PACKAGE_NAME = "com.mutualmobile.barricade";

  private static final ClassName TYPE_BARRICADE_RESPONSE_SET =
      ClassName.get(BarricadeResponseSet.class);
  private static final ParameterizedTypeName TYPE_CONFIG =
      ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class),
          ClassName.get(BarricadeResponseSet.class));

  private CodeGenerator() {
  }

  /**
   * Generates the code for the Barricade configuration based on the annotations found.
   *
   * @param processingEnv Processing environment
   * @param configs Configuration detected by annotation processing
   * @param messager Messager to print logs
   * @throws IOException
   */
  static void generateClass(ProcessingEnvironment processingEnv,
      HashMap<String, BarricadeResponseSet> configs, Messager messager) throws IOException {

    messager.printMessage(Diagnostic.Kind.NOTE, "Generating configuration code...");

    TypeSpec.Builder classBuilder = classBuilder(CLASS_NAME).addModifiers(PUBLIC, FINAL);

    FieldSpec valuesField = FieldSpec.builder(TYPE_CONFIG, "configs").addModifiers(PRIVATE).build();
    FieldSpec instanceField =
        FieldSpec.builder(ClassName.get(PACKAGE_NAME, CLASS_NAME), "barricadeConfig")
            .addModifiers(PRIVATE, STATIC)
            .build();

    MethodSpec.Builder instanceMethodBuilder = generateGetInstanceMethodBuilder();
    MethodSpec.Builder constructorMethodBuilder = generateConstructorBuilder(configs, messager);
    MethodSpec.Builder valuesMethod = generateGetConfigsMethodBuilder();
    MethodSpec.Builder getResponseMethodBuilder = generateGetResponseMethodBuilder();

    classBuilder.addType(generateEndpointsInnerClass(configs.keySet()));
    classBuilder.addType(generateResponsesInnerClass(configs));
    classBuilder.addField(instanceField);
    classBuilder.addField(valuesField);
    classBuilder.addMethod(instanceMethodBuilder.build());
    classBuilder.addMethod(constructorMethodBuilder.build());
    classBuilder.addMethod(valuesMethod.build());
    classBuilder.addMethod(getResponseMethodBuilder.build());

    classBuilder.addSuperinterface(IBarricadeConfig.class);

    JavaFile.Builder javaFileBuilder = builder(PACKAGE_NAME, classBuilder.build());
    JavaFile javaFile = javaFileBuilder.build();
    javaFile.writeTo(processingEnv.getFiler());

    messager.printMessage(Diagnostic.Kind.NOTE, "Code generation complete!");
  }

  private static TypeSpec generateEndpointsInnerClass(Set<String> endPoints) {
    TypeSpec.Builder classBuilder =
        classBuilder(ENDPOINTS_CLASS_NAME).addModifiers(PUBLIC, STATIC, FINAL);
    for (String endPoint : endPoints) {
      FieldSpec valuesField = FieldSpec.builder(String.class,
          StringUtils.removeAllSpecialCharacters(endPoint).toUpperCase())
          .addModifiers(PUBLIC, STATIC, FINAL)
          .initializer("$S", endPoint)
          .build();
      classBuilder.addField(valuesField);
    }
    return classBuilder.build();
  }

  private static TypeSpec generateResponsesInnerClass(
      HashMap<String, BarricadeResponseSet> configs) {

    TypeSpec.Builder classBuilder =
        classBuilder(RESPONSES_CLASS_NAME).addModifiers(PUBLIC, STATIC, FINAL);
    for (String endpoint : configs.keySet()) {
      classBuilder.addType(
          generateEndpointsResponsesInnerClass(endpoint, configs.get(endpoint).responses));
    }
    return classBuilder.build();
  }

  private static TypeSpec generateEndpointsResponsesInnerClass(String endpoint,
      List<BarricadeResponse> responses) {
    TypeSpec.Builder classBuilder =
        classBuilder(StringUtils.toCamelCase(endpoint)).addModifiers(PUBLIC, STATIC, FINAL);
    int count = 0;
    for (BarricadeResponse response : responses) {
      FieldSpec valuesField = FieldSpec.builder(int.class,
          StringUtils.removeAllSpecialCharactersAndExtensions(response.responseFileName).toUpperCase())
          .addModifiers(PUBLIC, STATIC, FINAL)
          .initializer("$L", count)
          .build();
      classBuilder.addField(valuesField);
      count++;
    }
    return classBuilder.build();
  }

  private static MethodSpec.Builder generateGetConfigsMethodBuilder() {
    return MethodSpec.methodBuilder("getConfigs")
        .returns(TYPE_CONFIG)
        .addModifiers(PUBLIC)
        .addStatement("return configs");
  }

  private static MethodSpec.Builder generateConstructorBuilder(
      HashMap<String, BarricadeResponseSet> values, Messager messager) {
    MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder().addModifiers(PUBLIC);
    methodBuilder.addStatement("configs = new HashMap<>()");

    for (Map.Entry<String, BarricadeResponseSet> entry : values.entrySet()) {
      BarricadeResponseSet barricadeResponseSet = entry.getValue();

      String listName = "barricadeResponsesFor" + entry.getKey();

      methodBuilder.addStatement("$T<$T> " + listName + " = new $T<>()", List.class,
          BarricadeResponse.class, ArrayList.class);

      for (BarricadeResponse barricadeResponse : barricadeResponseSet.responses) {
        methodBuilder.addStatement(listName + ".add(new $T($L, $S, $S))", BarricadeResponse.class,
            barricadeResponse.statusCode, barricadeResponse.responseFileName,
            barricadeResponse.contentType);
      }

      methodBuilder.addStatement(
          "configs.put($S, new $T(" + listName + ", " + barricadeResponseSet.defaultIndex + "))",
          entry.getKey(), TYPE_BARRICADE_RESPONSE_SET);
    }
    return methodBuilder;
  }

  private static MethodSpec.Builder generateGetInstanceMethodBuilder() {
    return MethodSpec.methodBuilder("getInstance")
        .returns(ClassName.get(PACKAGE_NAME, CLASS_NAME))
        .addModifiers(PUBLIC, STATIC)
        .addStatement("return barricadeConfig = barricadeConfig != null? barricadeConfig:"
            + " new BarricadeConfig()");
  }

  private static MethodSpec.Builder generateGetResponseMethodBuilder() {
    return MethodSpec.methodBuilder("getResponseForEndpoint")
        .addModifiers(PUBLIC)
        .addParameter(String.class, "endpoint")
        .returns(BarricadeResponse.class)
        .addStatement("$T responseSet = configs.get(endpoint)", BarricadeResponseSet.class)
        .addStatement("return responseSet.responses.get(responseSet.defaultIndex)");
  }
}
