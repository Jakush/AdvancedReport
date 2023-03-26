package me.retamrovec.advancedreport.config;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.debug.DebugReport;
import me.retamrovec.advancedreport.utils.Formatter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {

    private final AdvancedReport reportClass;
    public ConfigOptions(AdvancedReport reportClass) {
        this.reportClass = reportClass;
    }

    public String getString(String path) {
        return getString(path, null);
    }

    public String getString(String path, ConfigReplace configReplace) {
        String str = this.reportClass.getConfig().getString(path);
        if (str == null) {
            DebugReport.foundIssue("String in configuration with path " + path + " was not found!", Thread.currentThread().getStackTrace());
            return "String was not found in configuration with path: " + path;
        }
        if (configReplace != null) return configReplace.replace(str);
        return str;
    }

    public int getInt(String path) {
        return this.reportClass.getConfig().getInt(path);
    }

    public boolean getBoolean(String path) {
        return this.reportClass.getConfig().getBoolean(path);
    }

    public List<String> getStringList(String path) {
        return this.reportClass.getConfig().getStringList(path);
    }

    public List<Component> getComponentList(String path, ConfigReplace configReplace) {
        List<Component> list = new ArrayList<>();
        getStringList(path).forEach(str -> {
            if (configReplace == null) list.add(Formatter.chatColors(str));
            else list.add(Formatter.chatColors(configReplace.replace(str)));
        });
        return list;
    }
}
