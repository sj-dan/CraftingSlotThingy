package ca.danchan.trialproject;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ElytraEventsListener implements Listener {
    static Plugin plugin = getPlugin(Initializer.class);


    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        // Cancel any default behavior (i.e. cancel glide if player has no elytra, or touches the ground)
        event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        // If player is gliding, use the swap hands event (keybind F by default) as an accelerate key
        if (event.getPlayer().isGliding()) {
            event.setCancelled(true);
            event.getPlayer().setVelocity(event.getPlayer().getVelocity().multiply(1.05));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        // Disable death by running into wall while gliding
        if (event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        // If the player's sneaking, register a new event to slow them down until they stop sneaking, using sneak as brake
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getPlayer().isGliding() && event.getPlayer().isSneaking())
                    event.getPlayer().setVelocity(event.getPlayer().getVelocity().multiply(0.95));
                else
                    this.cancel();
            }
        }.runTaskTimerAsynchronously(plugin,0,1);
    }
}
