package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.UUID;

public class DatabaseManager {
    private static HikariDataSource ds;

    public static void init() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setDriverClassName("org.sqlite.JDBC");
        ds = new HikariDataSource(config);

        try (var con = ds.getConnection(); var st = con.createStatement()) {
            // Оставляем только UUID и ник-источник
            st.execute("""
                CREATE TABLE IF NOT EXISTS player_skins (
                    uuid TEXT PRIMARY KEY,
                    skin_nick TEXT NOT NULL
                )
            """);
        }
    }

    // Просто сохраняем, какой ник закреплен за игроком
    public static void saveSkin(UUID uuid, String nick) {
        try (var con = ds.getConnection();
             var ps = con.prepareStatement("INSERT OR REPLACE INTO player_skins (uuid, skin_nick) VALUES (?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, nick);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Получаем ник, чтобы потом скачать скин через PlayerSkin.fromUsername
    public static String getSkinNick(UUID uuid) {
        try (var con = ds.getConnection();
             var ps = con.prepareStatement("SELECT skin_nick FROM player_skins WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("skin_nick");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}