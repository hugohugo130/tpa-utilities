package com.hugo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("tpa_translations.json");

    public static JsonObject loadTranslations(MinecraftServer server) {

        if (!CONFIG_PATH.toFile().exists()) {
            createDefaultConfig(CONFIG_PATH.toFile());
            StateSaverAndLoader.resetPlayerState(server);
        }

        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    private static void createDefaultConfig(File configFile) {
        JsonObject defaultConfig = new JsonObject();
        JsonObject zh = new JsonObject();

        zh.addProperty("click_accept", "點擊此處！");
        zh.addProperty("error_tpa_yourself", "錯誤：您無法傳送請求給自己。");
        zh.addProperty("error_tpa_disabled", "該玩家已停用傳送請求功能。");
        zh.addProperty("error_tpa_already_asked", "錯誤：您已經向該玩家發送過傳送請求。");
        zh.addProperty("wants_tpa_teleport", "%s 想要傳送到您的位置！請使用 /tpaccept 接受，或");
        zh.addProperty("tpa_sent", "您的傳送請求已發送！");
        zh.addProperty("tpa_expired", "您的傳送請求已過期。");
        zh.addProperty("error_tpahere_yourself", "錯誤：您無法要求自己傳送至他處。");
        zh.addProperty("error_tpahere_disabled", "該玩家已停用接收傳送邀請功能。");
        zh.addProperty("error_tpahere_already_asked", "錯誤：您已經向該玩家發送過傳送邀請。");
        zh.addProperty("wants_tpahere_teleport", "%s 希望您傳送到他的位置！請使用 /tpaccept 接受，或");
        zh.addProperty("tpa_here_sent", "您的傳送邀請已發送！");
        zh.addProperty("tpahere_expired", "您的傳送邀請已過期。");
        zh.addProperty("teleport_success", "傳送成功！");
        zh.addProperty("error_tpaccept", "錯誤：沒有待接受的傳送請求。");
        zh.addProperty("tpa_refused", "您的傳送請求已被拒絕。");
        zh.addProperty("tpahere_refused", "您的傳送邀請已被拒絕。");
        zh.addProperty("error_tpadeny", "錯誤：沒有待拒絕的傳送請求。");
        zh.addProperty("tpacancel_success", "您所有的傳送請求與邀請已全部取消！");
        zh.addProperty("tpalock_activated", "傳送鎖定功能已啟用！");
        zh.addProperty("tpalock_deactivated", "傳送鎖定功能已停用！");
        zh.addProperty("tpalanguage_success", "傳送語言設定已更新！");
        zh.addProperty("tpalanguage_failure", "錯誤：提供的語言無效。");
        zh.addProperty("version", "1.0");

        defaultConfig.add("zh", zh);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(defaultConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
