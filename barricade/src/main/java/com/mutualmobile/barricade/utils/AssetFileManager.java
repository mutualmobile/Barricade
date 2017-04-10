/*
 * Copyright (c) 2011,2012,2013,2014,2015 Mutual Mobile. All rights reserved.
 */

package com.mutualmobile.barricade.utils;

import java.io.InputStream;

/**
 * API to fetch data from files located in the Android file system or the Java file system based on
 * where the code is being executed. For Android, files should be stored in the /assets folder in
 * the root of the project. A folder structure can be specified in file name parm with no leading /
 * such as: folderName/childFolderName/filename
 */
public interface AssetFileManager {

  String getContentsOfFileAsString(String fileName);

  InputStream getContentsOfFileAsStream(String fileName);

}

