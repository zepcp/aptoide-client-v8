package cm.aptoide.pt.filemanager;

import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.downloadmanager.DownloadManager;
import cm.aptoide.pt.utils.FileUtils;
import rx.Observable;

/**
 * Created by trinkes on 11/16/16.
 */

public class FileManager {

  private final CacheHelper cacheHelper;
  private final FileUtils fileUtils;
  private final String[] cacheFolders;
  private final DownloadManager downloadManager;
  private final L2Cache httpClientCache;

  public FileManager(CacheHelper cacheHelper, FileUtils fileUtils, String[] cacheFolders,
      DownloadManager downloadManager, L2Cache httpClientCache) {
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;
    this.cacheFolders = cacheFolders;
    this.downloadManager = downloadManager;
    this.httpClientCache = httpClientCache;
  }

  /**
   * deletes expired cache files
   */
  public Observable<Long> purgeCache() {
    return cacheHelper.cleanCache()
        .flatMap(cleaned -> downloadManager.clearAllDownloads()
            .map(success -> cleaned));
  }

  public Observable<Long> deleteCache() {
    return fileUtils.deleteFolder(cacheFolders)
        .flatMap(deletedSize -> {
          if (deletedSize > 0) {
            return downloadManager.clearAllDownloads()
                .map(success -> deletedSize);
          } else {
            return Observable.just(deletedSize);
          }
        })
        .doOnNext(aVoid -> {
          httpClientCache.clean();
        });
  }
}
