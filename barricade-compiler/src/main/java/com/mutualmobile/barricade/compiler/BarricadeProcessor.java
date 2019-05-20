package com.mutualmobile.barricade.compiler;

import com.google.auto.service.AutoService;
import com.mutualmobile.barricade.annotation.Barricade;
import com.mutualmobile.barricade.annotation.Params;
import com.mutualmobile.barricade.annotation.QueryParams;
import com.mutualmobile.barricade.annotation.RequestJson;
import com.mutualmobile.barricade.annotation.Response;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	@Override public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		try {
			HashMap<String, BarricadeResponseSet> configs = new HashMap<>();
			HashMap<String, Map<String, BarricadeResponse>> paramConfig = new HashMap<>();
			// Iterate over all @Barricade annotated elements
			for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Barricade.class)) {
				Barricade barricade = annotatedElement.getAnnotation(Barricade.class);
				messager.printMessage(NOTE, "[Barricade] Processing endpoint: " + barricade.endpoint());
				if (barricade.queryParams().length > 0) {
					Map<String, BarricadeResponse> responseMap = mapQueryParams(barricade);
					paramConfig.put(barricade.endpoint(), responseMap);
				} else if (barricade.requestJson().length > 0) {
					Map<String, BarricadeResponse> responseMap = mapRequestJson(barricade);
					paramConfig.put(barricade.endpoint(), responseMap);
				} else {
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
			}

			// This method is called multiple times, but we want to generate code only once
			if (!configs.isEmpty()||!paramConfig.isEmpty()) {
				generateCode(paramConfig,configs);
			}
		} catch (Exception e) {
			messager.printMessage(ERROR, "Couldn't process class:" + e.getMessage());
		}

		return true;
	}


	private Map<String, BarricadeResponse> mapQueryParams(Barricade barricade){
		Map<String, BarricadeResponse> responseMap = new HashMap<>(barricade.queryParams().length);
		for (QueryParams queryParams : barricade.queryParams()) {
			StringBuilder query = new StringBuilder();
			for (Params params : queryParams.params()) {
				query.append(params.name()).append("=").append(params.value()).append("&");
			}
			responseMap.put(query.toString(), new BarricadeResponse(queryParams.response()));
		}
		return responseMap;
	}

	private Map<String, BarricadeResponse> mapRequestJson(Barricade barricade){
		Map<String, BarricadeResponse> responseMap = new HashMap<>(barricade.queryParams().length);
		for (RequestJson postJson : barricade.requestJson()) {
			String query = postJson.body();
			responseMap.put(query, new BarricadeResponse(postJson.response()));
		}
		return responseMap;
	}

	private void generateCode(HashMap<String, Map<String,BarricadeResponse>> paramConfigs,HashMap<String, BarricadeResponseSet> configs) throws IOException {
		if (configs.isEmpty()&& paramConfigs.isEmpty()) {
			messager.printMessage(ERROR, "Couldn't find any endpoints");
		} else {
			CodeGenerator.generateClass(processingEnv,paramConfigs,configs, messager);
		}
	}
}
