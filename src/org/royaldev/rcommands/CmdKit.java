package org.royaldev.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

import java.util.HashMap;

public class CmdKit implements CommandExecutor {

    RoyalCommands plugin;

    public CmdKit(RoyalCommands instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("kit")) {
            if (!plugin.isAuthorized(cs, "rcmds.kit")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            if (plugin.getConfig().get("kits") == null) {
                cs.sendMessage(ChatColor.RED + "No kits defined!");
                return true;
            }
            String kitname = args[0].trim();
            if (plugin.getConfig().get("kits." + kitname) == null) {
                cs.sendMessage(ChatColor.RED + "That kit does not exist!");
                return true;
            }
            java.util.List<String> kits = plugin.getConfig().getStringList("kits." + kitname);
            if (kits == null) {
                cs.sendMessage(ChatColor.RED + "That kit does not exist!");
                return true;
            }
            if (plugin.kitPerms && !p.hasPermission("rcmds.kit." + kitname)) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that kit!");
                return true;
            }
            if (kits.size() < 1) {
                cs.sendMessage(ChatColor.RED + "That kit was configured wrong!");
                return true;
            }
            for (String s : kits) {
                String[] kit = s.trim().split(":");
                if (kit.length < 2) {
                    cs.sendMessage(ChatColor.RED + "That kit was configured wrong!");
                    return true;
                }
                int id;
                int amount;
                int data = -1;
                try {
                    id = Integer.parseInt(kit[0]);
                } catch (Exception e) {
                    cs.sendMessage(ChatColor.RED + "That kit was configured wrong!");
                    return true;
                }
                try {
                    amount = Integer.parseInt(kit[1]);
                } catch (Exception e) {
                    cs.sendMessage(ChatColor.RED + "That kit was configured wrong!");
                    return true;
                }
                try {
                    data = Integer.parseInt(kit[2]);
                } catch (Exception ignored) {
                }
                if (id < 1 || amount < 1) {
                    cs.sendMessage(ChatColor.RED + "That kit was configured wrong!");
                    return true;
                }
                if (Material.getMaterial(id) == null) {
                    cs.sendMessage(ChatColor.RED + "Invalid item ID in kit: " + ChatColor.GRAY + id);
                    return true;
                }
                ItemStack item;
                if (data > -1) {
                    item = new ItemStack(id, amount, (short) data);
                } else {
                    item = new ItemStack(id, amount);
                }
                HashMap<Integer, ItemStack> left = p.getInventory().addItem(item);
                if (!left.isEmpty()) {
                    for (ItemStack is : left.values()) {
                        p.getWorld().dropItemNaturally(p.getLocation(), is);
                    }
                }
            }
            p.sendMessage(ChatColor.BLUE + "Giving you the kit \"" + ChatColor.GRAY + kitname + ChatColor.BLUE + ".\"");
            return true;
        }
        return false;
    }
}
