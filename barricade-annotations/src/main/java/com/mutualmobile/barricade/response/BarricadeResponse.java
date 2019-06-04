package com.mutualmobile.barricade.response;

import com.mutualmobile.barricade.annotation.Response;

public class BarricadeResponse {
  public int statusCode;
  public String responseFileName;
  public String filePath;
  public String contentType;

  public BarricadeResponse(int statusCode, String responseFileName, String contentType,String filePath) {
    this.statusCode = statusCode;
    this.responseFileName = responseFileName;
    this.contentType = contentType;
    this.filePath = filePath;
  }

  public BarricadeResponse(Response response,String filePath) {
    this.statusCode = response.statusCode();
    this.responseFileName = response.fileName();
    this.contentType = response.type();
    this.filePath = filePath;
  }
}
