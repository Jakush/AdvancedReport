package me.retamrovec.advancedreport.config;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ConfigReplace {

    private final HashMap<String, String> placeholders;
    public ConfigReplace() {
        this.placeholders = new HashMap<>();
    }

    public ConfigReplace addPlaceholder(@NotNull Placeholder placeholder, String str) {
        this.placeholders.put("%" + placeholder.name().toLowerCase() + "%", str);
        return this;
    }

    protected String replace(String str) {
        String result = str;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public enum Placeholder {
        REASON,
        PLAYER_NAME,
        PLAYER_UUID,
        REPORTER_UUID,
        REPORTER_NAME
    }
}
