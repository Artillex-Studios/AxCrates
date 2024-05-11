package com.artillexstudios.axcrates.lang;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axcrates.AxCrates;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;

public class LanguageManager {
    private static Config translations;

    public static void reload() {
        final String lang = CONFIG.getString("language", "en_US").toLowerCase();
        final File file = new File(AxCrates.getInstance().getDataFolder(), "lib/translations/" + lang + ".yml");
        boolean exists = file.exists();
        translations = new Config(file);

        final List<String> versions = Version.getServerVersion().versions;
        final String version = versions.get(versions.size() - 1);
        if (exists && !translations.getBackingDocument().isEmpty(true) && translations.get("version", "").equals(version)) return;

        final String url = "https://api.github.com/repos/InventivetalentDev/minecraft-assets/contents/assets/minecraft/lang/" + lang + ".json?ref=" + version;
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00DDFF╠ &#00FFFFDownloading &f" + lang + " &#00FFFFlanguage files.. (version: " + version + ")"));

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final Gson gson = new GsonBuilder().create();
            final JsonObject object = gson.fromJson(response.body(), JsonObject.class);

            final String base64Content = object.get("content").getAsString();

            var gsonObject = gson.fromJson(new String(Base64Coder.decodeLines(base64Content)), JsonObject.class);

            for (Map.Entry<String, JsonElement> e : gsonObject.entrySet()) {
                if (e.getKey().startsWith("item.minecraft.")) {
                    final String name = e.getKey().replace("item.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }
                if (e.getKey().startsWith("block.minecraft.")) {
                    final String name = e.getKey().replace("block.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }
            }
            translations.set("version", version);
            translations.save();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String getTranslated(@NotNull Material material) {
        return translations.getString("material." + material.name().toLowerCase(), material.name().toLowerCase().replace("_", " "));
    }
}
