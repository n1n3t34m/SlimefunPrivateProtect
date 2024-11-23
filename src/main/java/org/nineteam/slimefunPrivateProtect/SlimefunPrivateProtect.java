package org.nineteam.slimefunPrivateProtect;

import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

import java.util.Arrays;

public final class SlimefunPrivateProtect extends JavaPlugin implements SlimefunAddon, CommandExecutor {
    static final Component no_permission_msg = MiniMessage.miniMessage().deserialize(
        "<gray>[<green>SF</green><aqua>P</aqua><blue>P</blue>]</gray> <red>You aren't allowed to do that here!</red>"
    );
    static int counter = 0;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BaseSlimefunListener(), this);

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
    public void onDisable() {
    }

    @Override
    public String getBugTrackerURL() {
        return null;
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName()) {
            case "check":
                if(sender instanceof Player p) {
                    final ComponentBuilder msg = Component.text();

                    // Header
                    String info = String.format("%s @ %d, %d, %d:",
                            p.getLocation().getWorld().getName(),
                            p.getLocation().getBlockX(),
                            p.getLocation().getBlockY(),
                            p.getLocation().getBlockZ());
                    msg.append(Component.text(info));

                    // Check permissions
                    for(Interaction i : Interaction.values()) {
                        boolean has_permission = Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), i);
                        msg.append(Component.text("\n" + i, has_permission ? NamedTextColor.GREEN : NamedTextColor.RED));
                    }

                    p.sendMessage(msg.build());
                } else return false;
            case "stats":
                sender.sendMessage("Prevented cases: " + counter);
            default:
                return false;
        }
    }

    public static void check(Cancellable e, Player p, Location l) {
        if(!Arrays.stream(Interaction.values()).allMatch(x -> Slimefun.getProtectionManager().hasPermission(p, l, x))) {
            p.sendMessage(no_permission_msg);
            e.setCancelled(true);
            counter++;
        }
    }

    public static <T extends PlayerEvent & Cancellable> void check(T e) {
        check(e, e.getPlayer(), e.getPlayer().getLocation());
    }
}
