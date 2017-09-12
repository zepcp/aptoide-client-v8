/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/06/2016.
 */

package cm.aptoide.pt.downloadmanager.exception;

public class DownloadNotFoundException extends RuntimeException {

  DownloadNotFoundException(String downloadHash) {
    super(String.format("Download not found for hash %s", downloadHash));
  }
}
