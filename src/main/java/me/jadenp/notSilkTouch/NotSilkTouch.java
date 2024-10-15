package me.jadenp.notSilkTouch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NotSilkTouch extends JavaPlugin implements Listener, CommandExecutor {

    private boolean requireSilkTouch;
    private boolean requireTool;

    private static final String COMMAND_NAME = "NotSilkTouch";

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand(COMMAND_NAME)).setExecutor(this);
        readConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void readConfig() {
        saveDefaultConfig();
        reloadConfig();
        requireSilkTouch = getConfig().getBoolean("require-silk-touch-enchantment");
        requireTool = getConfig().getBoolean("require-proper-tool");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && !event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.SURVIVAL
                && event.getPlayer().hasPermission("notsilktouch." + event.getBlock().getType().toString().toLowerCase())
        && ((!requireTool
                || (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR))
                && (!requireSilkTouch
                || (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH))))
        && event.getBlock().getLocation().getWorld() != null) {
            // drop item
            event.setDropItems(false);
            event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(event.getBlock().getType()));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase(COMMAND_NAME) && sender.isOp()) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                readConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded " + getDescription().getName() + " version " + getDescription().getVersion());
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GOLD + "/NotSilkTouch reload");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase(COMMAND_NAME) && sender.isOp() && args.length == 1)
            tab.add("reload");
        return tab;
    }
}
