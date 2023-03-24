package me.retamrovec.advancedreport.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Database {
    JavaPlugin plugin;
    Connection connection;
    public String table = "reports";
    public Database(JavaPlugin instance){
        plugin = instance;
    }

    public abstract Connection getConnection();

    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean isConnected();
    public PreparedStatement prepareStatement(String sql) {
        try {
            if (!isConnected()) connect();
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTable() {
        return table;
    }
}