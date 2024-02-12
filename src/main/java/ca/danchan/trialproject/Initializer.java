package ca.danchan.trialproject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Initializer extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register the events from all the files
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new InventoryLoopListener(), this);
        Bukkit.getPluginManager().registerEvents(new ElytraEventsListener(), this);
    }

    @Override
    public void onDisable() {
        /*
            If/when the plugin's disabled, clear the crafting slots of our items, to prevent
            items dropping right between the plugin being unloaded and the server shutting down
        */
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory cinv) {
                cinv.clear();
            }
        }
    }
}