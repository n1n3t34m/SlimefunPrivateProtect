package org.nineteam.slimefunPrivateProtect;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.sfiguz7.transcendence.implementation.items.UnstableItem;

public class TranscEndenceListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping unstable ingots
        if (SlimefunItem.getByItem(e.getItemDrop().getItemStack()) instanceof UnstableItem)
            SlimefunPrivateProtect.check(e);
    }
}
