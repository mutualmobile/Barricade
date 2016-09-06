package com.mutualmobile.barricade.response;

import com.mutualmobile.barricade.annotation.ResponseType;

/**
 * @author phaniraja.bhandari, 7/27/16.
 */

public class BarricadeResponse {
  public int statusCode;
  public String responseFileName;
  public String contentType;

  public BarricadeResponse(int statusCode, String responseFileName, String contentType) {
    this.statusCode = statusCode;
    this.responseFileName = responseFileName;
    this.contentType = contentType;
  }

  public BarricadeResponse(ResponseType responseType) {
    this.statusCode = responseType.statusCode();
    this.responseFileName = responseType.fileName();
    this.contentType = responseType.type();
  }
}
