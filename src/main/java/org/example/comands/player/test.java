package org.example.comands.player;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.example.api.blockbench.ObjLoader;
import org.example.api.blockbench.VertexBuffer;
import org.example.api.zero_command.ZeroCommand;

import java.util.ArrayList;
import java.util.List;

public class test extends Command implements ZeroCommand {

    private final List<Entity> spawnedEntities = new ArrayList<>();
    enum ModelAction {
        create, delete
    }
    public test() {
        super("test");
        setUsage("/test <create|delete>");

        var action = ArgumentType.Enum("action", ModelAction.class);

        addPlayerSyntax(((player, context) -> {
            String arg = context.get(action).toString();

            if (arg.equals("create")) {
                if (!spawnedEntities.isEmpty()) {
                    player.sendMessage(Component.text("Сначала удали текущую модель: /test delete"));
                    return;
                }

                var loc = player.getPosition();
                String objContent = ObjLoader.ObjLoad("/home/mihail/IdeaProjects/minestome/resourcepack/models/.xdp-cube.obj-8Nj4dd");
                VertexBuffer vertices = VertexBuffer.ObjLoad(objContent);

                vertices.getAll().forEach((vertex) -> {
                    Pos worldPos = vertex.getPosition().add(loc.x(), loc.y(), loc.z());

                    Entity display = new Entity(EntityType.TEXT_DISPLAY);
                    TextDisplayMeta meta = (TextDisplayMeta) display.getEntityMeta();
                    display.setNoGravity(true);
                    meta.setText(Component.text("•"));
                    meta.setBillboardRenderConstraints(TextDisplayMeta.BillboardConstraints.CENTER);
                    meta.setSeeThrough(true);
                    meta.setBackgroundColor(0);
                    display.setInstance(player.getInstance(), worldPos);
                    spawnedEntities.add(display);
                });

                player.sendMessage(Component.text("Создано " + spawnedEntities.size() + " вершин"));

            } else if (arg.equals("delete")) {
                if (spawnedEntities.isEmpty()) {
                    player.sendMessage(Component.text("Нечего удалять"));
                    return;
                }

                spawnedEntities.forEach(Entity::remove);
                spawnedEntities.clear();
                player.sendMessage(Component.text("Модель удалена"));

            } else {
                player.sendMessage(Component.text("Использование: /test <create|delete>"));
            }

        }), action);
    }

    @Override
    public Command getCommand() {
        return this;
    }
}