package com.github.ynverxe.windowadapter.nms.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NMSModuleLoader {

  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Class.class,
          (JsonDeserializer<Class<NMSModule>>) (jsonElement, type, jsonDeserializationContext) -> {
            try {
              return (Class<NMSModule>) Class.forName(jsonElement.getAsString());
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }).create();
  private static final Logger LOGGER = LoggerFactory.getLogger(NMSModuleLoader.class);
  public static final NMSModuleLoader INSTANCE = new NMSModuleLoader();

  private @Nullable NMSModule loadedModule;
  private boolean loadAttemptMade;
  private final Map<String, Class<NMSModule>> moduleTypeBySupportedVersions = new HashMap<>();

  private NMSModuleLoader() {
    InputStream stream = NMSModuleLoader.class.getClassLoader().getResourceAsStream("nms-modules.json");

    if (stream == null) {
      LOGGER.warn("Unable to find nms modules manifest.");
      return;
    }

    Reader reader = new InputStreamReader(stream);

    Map<Class<NMSModule>, List<String>> builtInModules = GSON.fromJson(reader, new TypeToken<Map<Class<NMSModule>, List<String>>>() {}.getType());
    if (builtInModules == null) {
      LOGGER.warn("Unable to load built-in modules.");
      return;
    }

    builtInModules.forEach((moduleClass,supportedVersions) -> supportedVersions.forEach(version -> this.registerModuleType(version, moduleClass)));

    LOGGER.info("{} built-in NMSModules types found", builtInModules.size());
  }

  public void registerModuleType(@NotNull String minecraftVersion, @NotNull Class<NMSModule> moduleType) {
    this.moduleTypeBySupportedVersions.put(
        Objects.requireNonNull(minecraftVersion, "minecraftVersion"),
        Objects.requireNonNull(moduleType, "moduleType"));
  }

  public @NotNull Optional<NMSModule> getLoadedModule() {
    tryLoadModule();

    return Optional.ofNullable(this.loadedModule);
  }

  public @NotNull NMSModule loadedModule() throws IllegalStateException {
    return getLoadedModule()
        .orElseThrow(() -> new IllegalStateException("No module loaded"));
  }

  @Internal
  public void tryLoadModule() {
    if (this.loadAttemptMade) return;

    this.loadAttemptMade = true;
    Class<NMSModule> nmsModuleClass = this.moduleTypeBySupportedVersions.get(Bukkit.getMinecraftVersion());

    if (nmsModuleClass == null) {
      throw new IllegalStateException("Cannot found a convenient NMSModule implementation for the current version");
    }

    try {
      this.loadedModule = nmsModuleClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}