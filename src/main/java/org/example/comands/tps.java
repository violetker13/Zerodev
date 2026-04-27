package org.example.comands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.monitoring.TickMonitor;
import org.example.Main;
import org.example.api.zero_command.ZeroCommand;

public class tps extends Command implements ZeroCommand {
    public tps() {
        super("tps");
        setUsage("/save");

        addPlayerSyntax((player,context)->{

        });
    }


    @Override
    public Command getCommand() {
        return this;
    }
}
