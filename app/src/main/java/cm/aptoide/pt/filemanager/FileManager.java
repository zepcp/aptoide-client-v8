package cm.aptoide.pt.filemanager;

import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.downloadmanager.base.DownloadManager;
import cm.aptoide.pt.utils.FileUtils;
import rx.Single;

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
  public Single<Long> purgeCache() {
    return cacheHelper.observeCleanCache()
        .doOnSuccess(__ -> downloadManager.removeAllDownloads());
  }

  public Single<Long> deleteCache() {
    return fileUtils.deleteFolder(cacheFolders)
        .flatMapSingle(deletedSize -> {
          if (deletedSize > 0) {
            downloadManager.removeAllDownloads();
          }
          return Single.just(deletedSize);
        })
        .doOnNext(__ -> httpClientCache.clean())
        .toSingle();
  }
}
