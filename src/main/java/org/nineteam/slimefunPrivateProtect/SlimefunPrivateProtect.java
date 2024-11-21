package org.nineteam.slimefunPrivateProtect;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

import java.util.Arrays;

public final class SlimefunPrivateProtect extends JavaPlugin implements SlimefunAddon, Listener {
    static final Component no_permission_msg = MiniMessage.miniMessage().deserialize(
        "<gray>[<green>SF</green><aqua>P</aqua><blue>P</blue>]</gray> <red>You aren't allowed to do that here!</red>"
    );

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

    public static <T extends PlayerEvent & Cancellable> void check(T e) {
        Player p = e.getPlayer();
        if(!Arrays.stream(Interaction.values()).allMatch(x -> Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), x))) {
            p.sendMessage(no_permission_msg);
            e.setCancelled(true);
        }
    }
}
