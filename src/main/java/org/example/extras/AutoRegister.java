package org.example.extras;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public class AutoRegister {
    public static void registerEvents(EventNode<Event> node, InstanceContainer instance, String... packages) {
        Reflections reflections = new Reflections((Object[]) packages);
        Set<Class<? extends EventHandler>> classes = reflections.getSubTypesOf(EventHandler.class);

        for (Class<? extends EventHandler> clazz : classes) {
            try {
                EventHandler handler = clazz.getDeclaredConstructor().newInstance();
                handler.register(node, instance);
                System.out.println("Зарегистрировано событие: " + clazz.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerCommands(String... packages) {
        Reflections reflections = new Reflections((Object[]) packages);
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> commandClass : commandClasses) {
            try {
                Command command = commandClass.getDeclaredConstructor().newInstance();
                MinecraftServer.getCommandManager().register(command);
                System.out.println("Зарегистрирована команда: " + commandClass.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}