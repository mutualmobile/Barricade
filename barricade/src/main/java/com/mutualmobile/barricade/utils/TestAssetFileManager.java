package com.mutualmobile.barricade.utils;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestAssetFileManager implements AssetFileManager {

  private static final String PATH_TO_DATA = "app/src/main/assets";
  private static final String PATH_TO_DATA_2 = "src/main/assets";

  @Override public String getContentsOfFileAsString(String fileName) {
    InputStream fos = null;
    try {
      fos = getContentsOfFileAsStream(fileName);
      byte[] fileData = new byte[fos.available()];
      fos.read(fileData);
      String dataFromFile = new String(fileData, "UTF-8");
      fos.close();
      return dataFromFile;
    } catch (IOException e) {
      System.out.println("Error occurred: " + e.getMessage());
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e1) {
          Log.e(this.getClass().getSimpleName(), "Could not close stream...", e1);
        }
      }
    }
    return null;
  }

  @Override public InputStream getContentsOfFileAsStream(String fileName) {
    File file = new File(String.format("%s/%s", PATH_TO_DATA, fileName));
    if(!file.exists()) {
      file = new File(String.format("%s/%s", PATH_TO_DATA_2, fileName));
    }
    InputStream fos = null;
    try {
      fos = new FileInputStream(file);
    } catch (IOException e) {
      System.out.println("Error occurred: " + e.getMessage());
    }
    return fos;
  }
}
