package com.mutualmobile.barricade.sample.api.model;

public class Repo {
  public long id;
  public String name;
  public String full_name;

  @Override public String toString() {
    return "Repo{\n"
        + "id="
        + id
        + ",\nname='"
        + name
        + '\''
        + ",\nfull_name='"
        + full_name
        + '\''
        + "}\n\n";
  }
}
