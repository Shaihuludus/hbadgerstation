package io.maddsoft.hbadgerstation;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Settings {

  private static final  Settings instance = new Settings();

  private final INIConfiguration iniConfiguration;

  private Settings() {
    iniConfiguration = new INIConfiguration();
    try (FileReader fileReader = new FileReader("config.ini")) {
      iniConfiguration.read(fileReader);
      iniConfiguration.setThrowExceptionOnMissing(false);
    } catch (IOException | ConfigurationException e) {
      log.error(e.getMessage());
    }
  }

  public static String getString(String key) {
    return instance.iniConfiguration.getString(key);
  }

  public static int getInt(String key) {
    return instance.iniConfiguration.getInt(key);
  }

  public static boolean getBoolean(String key) {
    return instance.iniConfiguration.getBoolean(key);
  }

  public static String getString(String key, String defaultValue) {
    return instance.iniConfiguration.getString(key, defaultValue);
  }

  public static int getInt(String key, int defaultValue) {
    return instance.iniConfiguration.getInt(key, defaultValue);
  }

  public static boolean getBoolean(String key, boolean defaultValue) {
    return instance.iniConfiguration.getBoolean(key, defaultValue);
  }

  public static Set<String> getSet(String key) {
    String value = instance.iniConfiguration.getString(key);
    if (StringUtils.isNotBlank(value)) {
      return Arrays.stream(value.split(","))
          .map(string-> string.trim().toLowerCase())
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }
}
