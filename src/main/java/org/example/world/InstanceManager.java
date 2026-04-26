package org.example.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.example.Main;
import org.example.extras.AutoRegister;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {

    private static final Map<Integer, InstanceContainer> instances = new HashMap<>();
    private static final Map<Integer, EventNode<InstanceEvent>> instanceNodes = new HashMap<>();

    public static void setupInstances(String[] args) {
        var instanceManager = MinecraftServer.getInstanceManager();
        int count = 2;

        if (args != null && args.length > 0) {
            try {
                count = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 1; i <= count; i++) {
            InstanceContainer inst = instanceManager.createInstanceContainer();
            inst.setChunkLoader(new AnvilLoader("worlds/world_" + i));
            instances.put(i, inst);

            GenWorld.init(inst);
            inst.setTime(0);
            inst.setWorldBorder(WorldBorder.DEFAULT_BORDER.withDiameter(128));
            final InstanceContainer finalInst = inst;
            EventNode<InstanceEvent> instanceNode = EventNode.type(
                    "instance-node-" + i,
                    EventFilter.INSTANCE,
                    (event, targetInstance) -> targetInstance.equals(finalInst)
            );

            instanceNodes.put(i, instanceNode);
            Main.node.addChild(instanceNode);
            AutoRegister.registerInstanceEvents(instanceNode, inst, "org.example.events.handlers");
            System.out.println("Инстанс " + i + " инициализирован");
        }
    }

    public static InstanceContainer getInstanceById(int id) {
        return instances.get(id);
    }

    public static EventNode<InstanceEvent> getNodeByInstanceId(int id) {
        return instanceNodes.get(id);
    }

    public static Map<Integer, InstanceContainer> getInstances() {
        return instances;
    }
}