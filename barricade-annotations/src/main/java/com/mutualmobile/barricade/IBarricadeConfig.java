package com.mutualmobile.barricade;

import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import java.util.HashMap;

/**
 * Contract for a Barricade configuration
 */
public interface IBarricadeConfig {
  HashMap<String, BarricadeResponseSet> getConfigs();

  BarricadeResponse getResponseForEndpoint(String endpoint);
}
