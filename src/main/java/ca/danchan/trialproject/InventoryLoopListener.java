package ca.danchan.trialproject;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Objects;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class InventoryLoopListener implements Listener {
    Plugin plugin = getPlugin(Initializer.class);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        /*
            When the player first joins, register a task to them that checks if their inventory is open every tick
            If it is open, run setItems which sets the 4 items in the crafting slots. Also, if player ends up being null
            (i.e. the player leaves), cancel the event.
        */

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player == null) {
                    this.cancel();
                    return;
                }
                if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory cInv) {
                    setItems(cInv,player);
                }
            }
        }.runTaskLater(plugin,  1);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        // When the player closes their inventory, clear the items in the crafting grid, to prevent dropping

        if (event.getInventory().getType() == InventoryType.CRAFTING)
            event.getInventory().clear();
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
                if (player == null) {
                    this.cancel();
                    return;
                }
                if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory cInv) {
                    setItems(cInv,player);
                }
            }
        }.runTaskLater(plugin,1);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {

        /*
            In ANY event that ANY of the non-droppable items end up dropped
            (i.e. death, player disconnect, or manual item drop), simply cancel the event
        */

        ItemMeta itemMeta = event.getEntity().getItemStack().getItemMeta();
        if (itemMeta != null && itemMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "nonDroppable"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRecipeBookClick(PlayerRecipeBookClickEvent event) {
        /*
            If the player clicks any recipe in recipe book, cancel it, since
            that will put the existing ingredients back into the player's inventory.
        */
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        // Get the ItemMeta for the item that was clicked, catch NullPointer in case the user clicks an empty spot

        ItemMeta itemMeta;
        try {
            itemMeta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
        } catch (NullPointerException e) {
            return;
        }

        // Confirm that the user is not in creative, that itemMeta is not empty, and that the item has the nonDroppable tag

        if (itemMeta != null && event.getWhoClicked().getGameMode() != GameMode.CREATIVE && itemMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "nonDroppable"))) {
            event.setCancelled(true);

            // Each button's function has been separated and can be found under ButtonFunctions.java, for organization's sake.

            switch (event.getSlot()) {
                case 1:
                    ButtonFunctions.buttonOne(event);
                    break;
                case 2:
                    ButtonFunctions.buttonTwo(event);
                    break;
                case 3:
                    ButtonFunctions.buttonThree(event);
                    break;
                case 4:
                    ButtonFunctions.buttonFour(event);
                    break;
                default:
                    event.setCurrentItem(new ItemStack(Material.AIR));
            }
            setItems((CraftingInventory)event.getClickedInventory(),Bukkit.getPlayer(event.getWhoClicked().getUniqueId()));
        }
    }

    public void setNonDroppable(ItemStack itemStack) {
        // Gets the item meta, adds the nonDroppable key, and updates the itemMeta.
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "nonDroppable"), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(itemMeta);
    }

    public void setSkullOwner(ItemStack item, Player player) {
        // Gets itemMeta, converts to SkullMeta, sets the SkullOwner, and then updates the itemMeta.
        SkullMeta skullInfo = (SkullMeta)item.getItemMeta();
        skullInfo.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        item.setItemMeta(skullInfo);
    }
    public void setItems(CraftingInventory cInv, Player player) {

        // Discord item

        ItemStack item1 = new ItemStack(Material.BLUE_DYE);
        setNonDroppable(item1);
        cInv.setItem(1, item1);

        // Stats item

        ItemStack item2 = new ItemStack(Material.PLAYER_HEAD);
        setNonDroppable(item2);
        setSkullOwner(item2, player);
        ItemMeta i2 = item2.getItemMeta();
        i2.displayName(Component.text("Your Stats", Style.style(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,false)));
        item2.setItemMeta(i2);
        cInv.setItem(2, item2);

        // Stats item

        ItemStack item3 = new ItemStack(Material.OAK_SIGN);
        setNonDroppable(item3);
        ItemMeta i3 = item3.getItemMeta();
        i3.displayName(Component.text("Our Developers!", Style.style(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false)));
        item3.setItemMeta(i3);
        cInv.setItem(3, item3);

        // Elytra item

        ItemStack item4 = new ItemStack(Material.ELYTRA);
        setNonDroppable(item4);
        ItemMeta i4 = item4.getItemMeta();
        i4.displayName(Component.text("Toggle Flight", Style.style(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC,false)));
        Component lore = Component.text("Currently: ",Style.style(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
        if (player.isGliding()) {
            lore = lore.append(Component.text("ON", Style.style(NamedTextColor.GREEN)));
            i4.addEnchant(Enchantment.MENDING, 1, true);
            i4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else
            lore = lore.append(Component.text("OFF",Style.style(NamedTextColor.RED)));
        i4.lore(Collections.singletonList(lore));
        item4.setItemMeta(i4);
        cInv.setItem(4,item4);
    }
}