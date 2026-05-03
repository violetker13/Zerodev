package org.example.comands.player;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.Instance;
import org.example.api.blockbench.ObjLoader;
import org.example.api.blockbench.VertexBuffer;
import org.example.api.blockbench.player.HeadPartModel;
import org.example.api.blockbench.player.Part;
import org.example.api.blockbench.player.PlayerBodyAttachment;
import org.example.api.blockbench.player.PlayerBodyModel;
import org.example.api.zero_command.ZeroCommand;

import java.util.*;

public class test2 extends Command implements ZeroCommand {
    private static final Map<UUID, PlayerBodyModel> activeBodies = new HashMap<>();

    private final List<Entity> spawnedEntities = new ArrayList<>();
    enum ModelAction {
        create, delete
    }
    public test2() {
        super("test2");
        setUsage("/test2 <msg>");

        addPlayerSyntax(((player, context) -> {
                player.getEntityMeta().setInvisible(true);
                onChat(player);
                player.sendMessage(Component.text("Создано " + spawnedEntities.size() + " вершин"));



        }));
    }
    public static void onChat(Player player) {




            UUID uuid = player.getUuid();

            // Если уже есть — убираем старое
            if (activeBodies.containsKey(uuid)) {
                activeBodies.get(uuid).remove();
            }

            // Спавним новое тело у ног игрока
            Instance instance = player.getInstance();
            Pos      origin   = player.getPosition(); // Y=0 системы — ноги игрока

            PlayerBodyModel body = new PlayerBodyModel();
            body.spawn(instance, origin);
            activeBodies.put(uuid, body);
            PlayerBodyAttachment.attach(player, body);
    }
    @Override
    public Command getCommand() {
        return this;
    }
}