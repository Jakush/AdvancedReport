package me.retamrovec.advancedreport.database;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.debug.DebugReport;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLite extends Database{

    String DATABASE;
    String HOST;
    String USER;
    String PASSWORD;
    int PORT;
    public SQLite(AdvancedReport reportClass) {
        super(reportClass);
        DATABASE = plugin.getConfig().getString("db.SQLite.database", "reports");
        HOST = plugin.getConfig().getString("db.SQLite.host", "0.0.0.0");
        USER = plugin.getConfig().getString("db.SQLite.user", "root");
        PASSWORD = plugin.getConfig().getString("db.SQLite.password", "");
        PORT = plugin.getConfig().getInt("db.SQLite.port", 3306);
    }

    public String createTable = "CREATE TABLE IF NOT EXISTS " + table + " (" +
            "`id` int NOT NULL," +
            "`reported` varchar(32) NOT NULL," +
            "`reporter` varchar(32) NOT NULL," +
            "`reason` varchar(32) NOT NULL," +
            "`timestamp` bigint NOT NULL" +
            ");";

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initConnectionLocal(String DATABASE) {
        File dataFolder = new File(plugin.getDataFolder(), DATABASE + ".db");
        if (!dataFolder.exists()){
            try {
                boolean ignored = dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + DATABASE + ".db");
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite JDBC library is missing.");
        }
    }

    public void initConnectionServer(String DATABASE, String HOST, String USER, String PASSWORD, int PORT) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USER, PASSWORD);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        }
    }

    public void connect() {
        if (plugin.getConfig().getBoolean("db.SQLite.useLocal")) initConnectionLocal(DATABASE);
        else initConnectionServer(DATABASE, HOST, USER, PASSWORD, PORT);
        if (isConnected()) {
            try (PreparedStatement ps = getConnection().prepareStatement(createTable)) {
                ps.executeUpdate();
            } catch (SQLException e) { DebugReport.foundMajor("Database couldn't finish task!", false, false, true); }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                DebugReport.foundMajor("Database couldn't finish task!", false, false, true);
            }
        }
    }
}