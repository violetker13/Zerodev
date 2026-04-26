package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.time.TimeUnit;
import org.example.extras.AutoRegister;
import org.example.world.GenWorld;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static EventNode<Event> node;
    public static InstanceContainer instance;
    public static Map<Integer, InstanceContainer> instances = new HashMap<>();
    public static Map<Integer, EventNode<InstanceEvent>> instanceNodes = new HashMap<>();
    public static MinecraftServer server;

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {
        server = MinecraftServer.init();
        node = EventNode.all("global");
        MinecraftServer.getGlobalEventHandler().addChild(node);

        var instanceManager = MinecraftServer.getInstanceManager();

        int count = 2;
        if (args != null && args.length > 0) {
            try {
                count = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 1; i <= count; i++) {
            InstanceContainer inst = instanceManager.createInstanceContainer();
            inst.setChunkLoader(new AnvilLoader("worlds/world_" + i)); // указываем папку
            instances.put(i, inst);
            GenWorld.init(inst);
            // Центр 0,0 размер 128
            inst.setWorldBorder(WorldBorder.DEFAULT_BORDER.withDiameter(128));
            final InstanceContainer finalInst = inst;
            EventNode<InstanceEvent> instanceNode = EventNode.type(
                    "instance-node-" + i,
                    EventFilter.INSTANCE,
                    (event, instance) -> instance.equals(finalInst)
            );

            instanceNodes.put(i, instanceNode);
            node.addChild(instanceNode);
            AutoRegister.registerInstanceEvents(instanceNode, inst, "org.example.events.handlers");

            System.out.println("Инстанс " + i + " создан");
        }

        instance = instances.get(1);
        AutoRegister.registerEvents(node, instance, "org.example.events.global");
        AutoRegister.registerCommands("org.example.comands");

        var scheduler = server.getSchedulerManager();

        server.start("0.0.0.0", 20000);
    }

    public static InstanceContainer getInstanceById(int id) {
        return instances.get(id);
    }

    public static EventNode<InstanceEvent> getNodeByInstanceId(int id) {
        return instanceNodes.get(id);
    }
}