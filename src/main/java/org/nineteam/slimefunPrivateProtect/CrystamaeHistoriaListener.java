package org.nineteam.slimefunPrivateProtect;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.sefiraat.crystamaehistoria.slimefun.CrystaStacks;

public class CrystamaeHistoriaListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        // Prevent stealing items with voids
        if(!e.isSneaking()) return;
        if(SlimefunUtils.containsSimilarItem(e.getPlayer().getInventory(), CrystaStacks.DISPLACED_VOID, false))
            SlimefunPrivateProtect.check(e);
    }
}
