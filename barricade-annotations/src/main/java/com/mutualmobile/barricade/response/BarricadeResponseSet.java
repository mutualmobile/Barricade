package com.mutualmobile.barricade.response;

import java.util.List;

/**
 * Contains all responses for a barricaded endpoint along with other metadata.
 *
 * @author Mustafa Ali, 26/08/16.
 */
public class BarricadeResponseSet {
  public List<BarricadeResponse> responses;
  public int defaultIndex;
  public int originalDefaultIndex;

  public BarricadeResponseSet(List<BarricadeResponse> responses, int defaultIndex) {
    this.responses = responses;
    this.defaultIndex = defaultIndex;
    this.originalDefaultIndex = defaultIndex;
  }
}
