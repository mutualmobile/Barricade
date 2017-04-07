package com.mutualmobile.barricade.response;

import com.mutualmobile.barricade.annotation.Response;

public class BarricadeResponse {
  public int statusCode;
  public String responseFileName;
  public String contentType;

  public BarricadeResponse(int statusCode, String responseFileName, String contentType) {
    this.statusCode = statusCode;
    this.responseFileName = responseFileName;
    this.contentType = contentType;
  }

  public BarricadeResponse(Response response) {
    this.statusCode = response.statusCode();
    this.responseFileName = response.fileName();
    this.contentType = response.type();
  }
}
