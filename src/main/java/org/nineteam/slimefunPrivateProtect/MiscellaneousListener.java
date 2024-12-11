package org.nineteam.slimefunPrivateProtect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class MiscellaneousListener implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerFishEvent(PlayerFishEvent e) {
		// Prevent moving entities with fishing rod as some protection plugins fails at that
		//  for example HuskTowns: https://github.com/WiIIiam278/HuskTowns/issues/534
		// NOTE: This method is easy but has a visual side effect of a hook remaining on entity
		if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
			SlimefunPrivateProtect.check(e, e.getPlayer(), e.getHook().getLocation());
	}
}
