package org.example.events;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.InstanceContainer;

// Базовый класс который должны наследовать все события
public abstract class EventHandler {
    public abstract void register(EventNode<Event> node, InstanceContainer instance);
}