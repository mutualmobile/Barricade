/*
 * Copyright (c) 2011,2012,2013,2014,2015 Mutual Mobile. All rights reserved.
 */

package com.mutualmobile.barricade.utils;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Implementation of {@link AssetFileManager} to allow reading files from assets folder in Android
 */
public class AndroidAssetFileManager implements AssetFileManager {
  private Context mApplicationContext;
  private static final String TAG = "AndroidAssetFileManager";

  public AndroidAssetFileManager(Context applicationContext) {
    mApplicationContext = applicationContext;
  }

  public String getContentsOfFileAsString(String fileName) {
    try {
      InputStream is = getContentsOfFileAsStream(fileName);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int aByte = is.read();
      while (aByte != -1) {
        bos.write(aByte);
        aByte = is.read();
      }
      return bos.toString("UTF-8");
    } catch (IOException e) {
      Logger.getLogger(TAG).severe(e.getMessage());
    }
    return null;
  }

  @Override public InputStream getContentsOfFileAsStream(String fileName) {
    try {
      return mApplicationContext.getAssets().open(String.format("%s", fileName));
    } catch (IOException e) {
      Logger.getLogger(TAG).severe(e.getMessage());
    }
    return null;
  }
}

