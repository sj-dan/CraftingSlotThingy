package ca.danchan.trialproject;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.invui.animation.Animation;
import xyz.xenondevs.invui.animation.impl.SequentialAnimation;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.util.MojangApiUtils;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ButtonFunctions {
    static Plugin plugin = getPlugin(Initializer.class);

    public static void buttonOne(InventoryClickEvent event) {

        // lol hardcoded discord link

        event.getWhoClicked().sendMessage(Component.text("Click here to join our Discord! (discord.gg/clubmine)", Style.empty()
                .color(TextColor.color(0x7289da))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/clubmine"))));


        // Close inventory 1 tick later for race condition avoidance

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getInventory().close();
            }
        }.runTaskLater(plugin,1);
    }
    public static void buttonTwo(InventoryClickEvent event) {

        // Play time item

        ItemStack playTime = new ItemStack(Material.CLOCK);
        ItemMeta t = playTime.getItemMeta();
        t.displayName(Component.text("Play time",Style.empty()
                .decoration(TextDecoration.ITALIC, false)));
        t.lore(Collections.singletonList(Component.text(Bukkit.getOfflinePlayer(event.getWhoClicked().getUniqueId()).getStatistic(Statistic.TOTAL_WORLD_TIME)/1200).append(Component.text(" minutes"))));
        playTime.setItemMeta(t);

        // Death count item

        ItemStack deathCount = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta s = deathCount.getItemMeta();
        s.displayName(Component.text("Death count",Style.empty()
                .decoration(TextDecoration.ITALIC, false)));
        s.lore(Collections.singletonList(Component.text(Bukkit.getOfflinePlayer(event.getWhoClicked().getUniqueId()).getStatistic(Statistic.DEATHS)).append(Component.text(" deaths"))));
        deathCount.setItemMeta(s);

        // Build GUI

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . 1 . 2 . . #",
                        "# # # # # # # # #")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('1', playTime)
                .addIngredient('2', deathCount)
                .build();

        // Set window for GUI

        Window window = Window.single()
                .setViewer(Objects.requireNonNull(Bukkit.getPlayer(event.getWhoClicked().getUniqueId())))
                .setTitle("Player stats for: "+event.getWhoClicked().getName())
                .setGui(gui)
                .build();

        // Show GUI to player 1 tick after click to prevent race condition

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getInventory().close();
                window.open();
            }
        }.runTaskLater(plugin,1);
    }

    public static void buttonThree(InventoryClickEvent event) {

        // Build GUI, catch Mojang API exceptions for player skulls

        Gui gui = null;
        try {
            gui = Gui.normal()
                    .setStructure(
                            "# $ # $ # $ # $ #",
                            "$ . 1 . 2 . 3 . $",
                            "# $ # $ # $ # $ #")
                    .addIngredient('#', new SimpleItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("\u00a7r\u00a7dMine!")))
                    .addIngredient('$', new SimpleItem(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setDisplayName("\u00a7r\u00a7bClub!")))
                    .addIngredient('1', new SimpleItem(new SkullBuilder("Notch").setDisplayName("\u00a7r\u00a76Notch")))
                    .addIngredient('2', new SimpleItem(new SkullBuilder("Herobrine").setDisplayName("\u00a7r\u00a74Herobrine")))
                    .addIngredient('3', new SimpleItem(new SkullBuilder(UUID.fromString("382ce431-0a6e-4070-91bb-154903e3e4d1")).setDisplayName("\u00a7r\u00a7aepic plugin dev (SJ_Dan)")))
                    .build();
        } catch (MojangApiUtils.MojangApiException | IOException ignore) {}

        // Set window for GUI

        Window window = Window.single()
                .setViewer(Objects.requireNonNull(Bukkit.getPlayer(event.getWhoClicked().getUniqueId())))
                .setTitle("Player stats for: "+event.getWhoClicked().getName())
                .setGui(gui)
                .build();

        // Show GUI to player 1 tick after click to prevent race condition

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getInventory().close();
                window.open();
            }
        }.runTaskLater(plugin,1);
    }

    public static void buttonFour(InventoryClickEvent event) {

        // Toggle gliding value, store new gliding value as boolean for next part

        boolean newGlidingValue = !event.getWhoClicked().isGliding();
        event.getWhoClicked().setGliding(newGlidingValue);

        // If the user JUST started gliding, start a new async task that shows them actionbar

        if (newGlidingValue) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (event.getWhoClicked().isGliding()) {
                        event.getWhoClicked().sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofSeconds(2147483647), Duration.ZERO));
                        event.getWhoClicked().sendActionBar(Component.text("Press ").append(Component.keybind("key.swapOffhand"))
                                .append(Component.text(" for gas, ")).append(Component.keybind("key.sneak")).append(Component.text(" to brake!"))
                                .style(Style.empty().color(NamedTextColor.GREEN)));
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(plugin,0,20);
        }
    }
}
