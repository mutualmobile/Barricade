package com.mutualmobile.barricade.sample.api.model;

import com.google.gson.annotations.SerializedName;

public class Joke {
  public String id;
  public String value;
  @SerializedName("iconUrl")
  public String iconUrl;
  public String category;
  public String url;

  @Override public String toString() {
    return "Joke{\n"
        + "id="
        + '\''
        +id
        +'\''
        +"\nJoke="
        +'\''
        +value
        +'\''
        + "}\n\n";
  }
}
