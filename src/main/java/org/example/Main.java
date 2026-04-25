package org.example;


import net.minestom.server.MinecraftServer;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.InstanceContainer;
import org.example.extras.AutoRegister;
import org.example.world.GenWorld;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static EventNode<Event> node; // убери инициализацию здесь
    // Для обратной совместимости
    public static InstanceContainer instance;
    // Хранилище для всех инстансов: id -> instance
    public static Map<Integer, InstanceContainer> instances = new HashMap<>();
    public static MinecraftServer server;

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args){
        server = MinecraftServer.init();

        node = EventNode.all("global");

        var instanceManager = MinecraftServer.getInstanceManager();

        // Определяем количество инстансов: из аргумента, системного свойства или по умолчанию 2
        int count = 2;
        if (args != null && args.length > 0) {
            try {
                count = Integer.parseInt(args[0]);
                if (count < 1) count = 1;
            } catch (NumberFormatException ignored) {}
        } else {
            String prop = System.getProperty("instances");
            if (prop != null) {
                try {
                    count = Integer.parseInt(prop);
                    if (count < 1) count = 1;
                } catch (NumberFormatException ignored) {}
            }
        }

        // Создаём и инициализируем инстансы
        for (int i = 1; i <= count; i++) {
            InstanceContainer inst = instanceManager.createInstanceContainer();
            instances.put(i, inst);
            GenWorld.init(inst);
            // Регистрируем обработчики событий для этого инстанса
            AutoRegister.registerEvents(node, inst, "org.example.events.handlers");
            System.out.println("Инстанс " + i + " создан и инициализирован");
        }

        // legacy: пусть instance ссылается на первый инстанс
        instance = instances.get(1);

        MinecraftServer.getGlobalEventHandler().addChild(node);

        // Команды регистрируем один раз
        AutoRegister.registerCommands("org.example.comands");

        server.start("0.0.0.0", 20000);
    }

    public static InstanceContainer getInstanceById(int id) {
        return instances.get(id);
    }
}
