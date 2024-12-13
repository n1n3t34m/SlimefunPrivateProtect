package org.nineteam.slimefunPrivateProtect;

import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class SlimefunPrivateProtect extends JavaPlugin implements SlimefunAddon, CommandExecutor {
    static final Component prefix = MiniMessage.miniMessage().deserialize(
            "<gray>[<green>SF</green><aqua>P</aqua><blue>P</blue>]</gray> "
    );
    static int counter = 0;
    static Plugin instance;
    static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        new Metrics(this, 24131);  // bStats

        Bukkit.getPluginManager().registerEvents(new BaseSlimefunListener(), this);
        Bukkit.getPluginManager().registerEvents(new MiscellaneousListener(), this);

        if(Bukkit.getPluginManager().isPluginEnabled("TranscEndence")) {
            Bukkit.getPluginManager().registerEvents(new TranscEndenceListener(), this);
            getLogger().info("TranscEndence guard enabled!");
        }
        if(Bukkit.getPluginManager().isPluginEnabled("CrystamaeHistoria")) {
            Bukkit.getPluginManager().registerEvents(new CrystamaeHistoriaListener(), this);
            getLogger().info("CrystamaeHistoria guard enabled!");
        }
    }

	@Override
    public String getBugTrackerURL() {
        return null;
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName()) {
            case "check":
                if (sender instanceof Player p) {
                    final ComponentBuilder msg = text();

                    // Header
                    String info = String.format("%s @ %d, %d, %d:",
                            p.getLocation().getWorld().getName(),
                            p.getLocation().getBlockX(),
                            p.getLocation().getBlockY(),
                            p.getLocation().getBlockZ());
                    msg.append(prefix).append(text(info));

                    // Check permissions
                    for (Interaction i : Interaction.values()) {
                        boolean has_permission = Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), i);
                        msg.append(text("\n" + i, has_permission ? GREEN : RED));
                    }

                    p.sendMessage(msg.build());
                } else sender.sendMessage(prefix.append(text("Player required!")));
                return true;
            case "stats":
                sender.sendMessage(prefix.append(text("Prevented cases: " + counter)));
                return true;
        }
        return false;
    }

    public static void check(Cancellable e, Player p, Location l) {
        if(!Arrays.stream(Interaction.values()).allMatch(x -> Slimefun.getProtectionManager().hasPermission(p, l, x))) {
            p.sendMessage(prefix.append(text("You aren't allowed to do that here!", RED)));
            e.setCancelled(true);
            counter++;
        }
    }

    public static <T extends PlayerEvent & Cancellable> void check(T e) {
        check(e, e.getPlayer(), e.getPlayer().getLocation());
    }
}
