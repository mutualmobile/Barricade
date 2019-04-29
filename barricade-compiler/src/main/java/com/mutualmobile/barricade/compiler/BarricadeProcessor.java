package com.mutualmobile.barricade.compiler;

import com.google.auto.service.AutoService;
import com.mutualmobile.barricade.annotation.Barricade;
import com.mutualmobile.barricade.annotation.Response;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * Annotation processor for Barricade annotations.
 */
@AutoService(Processor.class) public class BarricadeProcessor extends AbstractProcessor {

  private Messager messager;

  @Override public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    messager = processingEnvironment.getMessager();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    return singleton(Barricade.class.getCanonicalName());
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    try {
      HashMap<String, BarricadeResponseSet> configs = new HashMap<>();
      // Iterate over all @Barricade annotated elements
      for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Barricade.class)) {
        Barricade barricade = annotatedElement.getAnnotation(Barricade.class);
        messager.printMessage(NOTE, "[Barricade] Processing endpoint: " + barricade.endpoint());

        List<BarricadeResponse> responses = new ArrayList<>(barricade.responses().length);
        int defaultIndex = 0;

        for (int i = 0; i < barricade.responses().length; i++) {
          Response option = barricade.responses()[i];
          responses.add(new BarricadeResponse(option));
          if (option.isDefault()) {
            defaultIndex = i;
          }
        }
        configs.put(barricade.endpoint(), new BarricadeResponseSet(responses, defaultIndex));
      }

      // This method is called multiple times, but we want to generate code only once
      if (!configs.isEmpty()) {
        generateCode(configs);
      }
    } catch (Exception e) {
      messager.printMessage(ERROR, "Couldn't process class:" + e.getMessage());
    }

    return true;
  }

  private void generateCode(HashMap<String, BarricadeResponseSet> configs) throws IOException {
    if (configs.isEmpty()) {
      messager.printMessage(ERROR, "Couldn't find any endpoints to barricade");
    } else {
      CodeGenerator.generateClass(processingEnv, configs, messager);
    }
  }
}
