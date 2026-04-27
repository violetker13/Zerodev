package org.example.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.example.Main;
import org.example.extras.AutoRegister;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InstanceManager {

    private static final Map<Worlds, InstanceContainer> instances = new HashMap<>();
    private static final Map<Worlds, EventNode<InstanceEvent>> instanceNodes = new HashMap<>();

    public static void setupInstances(String[] args) {
        var instanceManager = MinecraftServer.getInstanceManager();

        for (Worlds element : Worlds.values()) {
            InstanceContainer instance = instanceManager.createInstanceContainer();
            instance.setChunkLoader(new AnvilLoader("worlds/world_" + element));
            instances.put(element, instance);

            //GenWorld.init(instance);
            instance.setTime(0);
            instance.setWorldBorder(WorldBorder.DEFAULT_BORDER.withDiameter(128));

            // Создаём node с фильтром именно для этого инстанса
            EventNode<InstanceEvent> instanceNode = EventNode.type(
                    "instance-node-" + element.name(),
                    EventFilter.INSTANCE,
                    (event, targetInstance) -> targetInstance.equals(instance)  // можно использовать instance напрямую
            );

            instanceNodes.put(element, instanceNode);
            Main.node.addChild(instanceNode);

            AutoRegister.registerInstanceEvents(instanceNode, instance, "org.example.events.handlers");

            System.out.println("Инстанс " + element + " инициализирован");
        }






    }

    public static InstanceContainer getInstanceById(Worlds id) {
        return instances.get(id);
    }

    public static EventNode<InstanceEvent> getNodeByInstanceId(Worlds id) {
        return instanceNodes.get(id);
    }

    public static InstanceContainer getInstanceById(String idStr) {
        return instances.entrySet().stream()
                .filter(e -> e.getKey().name().equalsIgnoreCase(idStr))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
    public static String getIdByInstance(Instance instance) {
        return instances.entrySet().stream()
                .filter(entry -> entry.getValue().equals(instance))
                .map(entry -> entry.getKey().name())
                .findFirst()
                .orElse("unknown"); // Возвращаем "unknown", если инстанс не найден в базе
    }
    public static Map<Worlds, InstanceContainer> getInstances() {
        return instances;                    // возвращаем как есть
    }

    // Или если хочешь Map<String, ...> для команд:
    public static Map<String, InstanceContainer> getInstancesAsStringKey() {
        Map<String, InstanceContainer> map = new HashMap<>();
        instances.forEach((world, inst) -> map.put(world.name(), inst)); // или world.toString()
        return map;
    }
}