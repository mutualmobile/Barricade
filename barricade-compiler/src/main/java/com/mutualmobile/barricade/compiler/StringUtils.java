package com.mutualmobile.barricade.compiler;

/**
 * Used to create proper class and variable names for BarricadeConfig based on response files
 */
class StringUtils {
  static String toCamelCase(String str) {

    str = str.replaceAll("[^\\w]+", "");

    return generateCamelString(generateCamelString(str, " "), "_").replace(" ", "");
  }

  private static String generateCamelString(String str, String separator) {

    String[] words = str.split(separator);
    StringBuilder sb = new StringBuilder();
    if (words[0].length() > 0) {
      sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
      for (int i = 1; i < words.length; i++) {
        sb.append(" ");
        sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
      }
    }
    return sb.toString();
  }

  static String removeAllSpecialCharactersAndExtensions(String string) {
    String temp = string.substring(0, string.lastIndexOf("."));
    return removeAllSpecialCharacters(temp);
  }

  static String removeAllSpecialCharacters(String string) {
    return string.replaceAll("[^\\w_]+", "").replaceAll(" ", "");
  }
}
