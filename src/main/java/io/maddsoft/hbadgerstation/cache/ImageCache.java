package io.maddsoft.hbadgerstation.cache;

import io.maddsoft.hbadgerstation.Settings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;

@Slf4j
public class ImageCache {

  @Getter
  private static final ImageCache imageCache = new ImageCache();

  private boolean cacheEnabled;

  private ImageCache() {
    cacheEnabled = Settings.getInt("cache", "cacheOn", 1) == 1;
    if (cacheEnabled) {
      File cacheDirectory = new File(Settings.getString(Settings.SECTION_CACHE, "cacheDirectory", "./cache"));
      if (!cacheDirectory.exists()){
        cacheEnabled = cacheDirectory.mkdir();
      }
    }
  }

  public String getCachedImage(String imageName, int width, int height) {
    if (!cacheEnabled) {
      return imageName;
    }
    String inCacheName = generateInCacheName(imageName.replace("\\", "/"), width, height);
    File cachedImageFile = new File(inCacheName);
    if(cachedImageFile.exists()){
      return cachedImageFile.getAbsolutePath();
    }
    try {
      if (cachedImageFile.getParentFile().exists() || cachedImageFile.getParentFile().mkdirs()) {
        scaleImage(imageName, cachedImageFile, width, height);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return imageName;
    }
    return cachedImageFile.getAbsolutePath();
  }

  private void scaleImage(String imageName, @NonNull File fileInCache, int width, int height) throws IOException {
    System.out.println("Scaling " + fileInCache.getAbsolutePath());
    BufferedImage originalImage = ImageIO.read(new File(imageName));
    BufferedImage resizedImage = Scalr.resize(originalImage, Mode.AUTOMATIC, width, height);
    ImageIO.write(resizedImage, "jpg", fileInCache);
  }

  private String generateInCacheName(String imageName, int width, int height) {
    return Settings.getString(Settings.SECTION_CACHE, "cacheDirectory", "./cache")
        + "/"
        + StringUtils.substringBeforeLast(imageName.replace(":", ""), "/")
        + "/"
        + width + "_" + height
        + "_"
        + StringUtils.substringAfterLast(imageName, "/").replace("//", "/");
  }
}
