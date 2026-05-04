package org.example;

import me.lucko.spark.minestom.SparkMinestom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.example.extras.extra.Splitter;
import org.example.database.SkinDatabaseManager;
import org.example.extras.extra.AnimationConverter;
import org.example.extras.AutoRegister;
import org.example.extras.extra.ResourcePack;
import org.example.world.InstanceManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static MinecraftServer server;
    public static EventNode<Event> node;

    public static final ArrayList<String> ADMINS = new ArrayList<>() {{ add("kaleb_b"); }};


    public static void main(String[] args) {
        server = MinecraftServer.init();
        initSystems();
        start(args);
    }

    private static void initSystems() {
        try {
            SkinDatabaseManager.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Path directory = Path.of("spark");

        SparkMinestom.builder(directory)
                .commands(true)
                .permissionHandler((sender, permission) -> true)
                .enable();
        try {
            new Splitter();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ResourcePack.init();
        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage(Component.text("Команда /" + command + " не найдена!", NamedTextColor.RED));

        });
    }
    public static void start(String[] args) {
        node = EventNode.all("global");
        MinecraftServer.getGlobalEventHandler().addChild(node);
        InstanceManager.setupInstances(args);
        AutoRegister.registerEvents(node, InstanceManager.getInstanceById("auth"), "org.example.events.global");
        AutoRegister.registerCommands("org.example.comands");
        CompletableFuture.runAsync(AnimationConverter::run);
        server.start("0.0.0.0", 20000);
    }
}