package org.nineteam.slimefunPrivateProtect.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.sfiguz7.transcendence.implementation.items.UnstableItem;
import org.nineteam.slimefunPrivateProtect.SlimefunPrivateProtect;

public class TranscEndenceListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping unstable ingots
        if (SlimefunItem.getByItem(e.getItemDrop().getItemStack()) instanceof UnstableItem)
            SlimefunPrivateProtect.check(e);
    }
}
