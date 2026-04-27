package org.example;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.example.database.DatabaseManager;
import org.example.extras.AutoRegister;
import org.example.extras.Utils;
import org.example.world.InstanceManager;

import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static MinecraftServer server;
    public static EventNode<Event> node;

    public static final ArrayList<String> ADMINS = new ArrayList<>() {{ add("kaleb_b"); }};
    public static String packUrl = null;
    public static String packHash = null;

    public static void main(String[] args) {
        server = MinecraftServer.init();
        initSystems();
        start(args);
    }

    private static void initSystems() {
        // БД
        try {
            DatabaseManager.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Ресурспак
        try {
            Path packPath = Path.of("resourcepack/pack.zip");
            //packUrl = Utils.uploadPack(packPath);
            packHash = Utils.sha1Hash(packPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Неизвестные команды
        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage(Component.text("Команда /" + command + " не найдена!", NamedTextColor.RED));
        });
    }

    public static void start(String[] args) {
        node = EventNode.all("global");
        MinecraftServer.getGlobalEventHandler().addChild(node);

        // Авто-скины

        // Работа с инстансами через менеджер
        InstanceManager.setupInstances(args);

        // Регистрация
        AutoRegister.registerEvents(node, InstanceManager.getInstanceById("auth"), "org.example.events.global");

        AutoRegister.registerCommands("org.example.comands");

        server.start("0.0.0.0", 20000);
    }
}