package org.nineteam.slimefunPrivateProtect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.Arrays;

public final class SlimefunPrivateProtect extends JavaPlugin implements SlimefunAddon, Listener {
    final Component no_permission_msg = MiniMessage.miniMessage().deserialize(
        "<gray>[<green>SF</green><aqua>P</aqua><blue>P</blue>]</gray> <red>You aren't allowed to do that here!</red>"
    );

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
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

    public <T extends PlayerEvent & Cancellable> void check(T e) {
        Player p = e.getPlayer();
        if(!Arrays.stream(Interaction.values()).allMatch(x -> Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), x))) {
            p.sendMessage(no_permission_msg);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping radioactive materials
        if(SlimefunUtils.isRadioactive(e.getItemDrop().getItemStack())) check(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        // Prevent stealing items with magnets
        if(e.getPlayer().getInventory().contains(SlimefunItems.INFUSED_MAGNET)) check(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        // Prevent trolling with scroll on dimensional teleposition which can interrupt afk
        if(SlimefunUtils.isItemSimilar(e.getItem(), SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION, false)) check(e);
    }
}
