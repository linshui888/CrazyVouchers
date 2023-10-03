package com.badbones69.crazyvouchers.paper.commands;

import com.badbones69.crazyvouchers.paper.CrazyVouchers;
import com.badbones69.crazyvouchers.paper.listeners.VoucherMenuListener;
import com.badbones69.crazyvouchers.paper.api.CrazyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoucherTab implements TabCompleter {

    private final CrazyVouchers plugin = JavaPlugin.getPlugin(CrazyVouchers.class);

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final VoucherMenuListener voucherMenuListener = this.plugin.getGui();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) { // /voucher
            if (hasPermission(sender, "admin")) completions.add("help");
            if (hasPermission(sender, "admin")) completions.add("list");
            if (hasPermission(sender, "redeem")) completions.add("redeem");
            if (hasPermission(sender, "admin")) completions.add("give");
            if (hasPermission(sender, "admin")) completions.add("giveall");
            if (hasPermission(sender, "admin")) completions.add("open");
            if (hasPermission(sender, "admin")) completions.add("reload");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /voucher arg0
            switch (args[0].toLowerCase()) {
                case "redeem" -> {
                    // Only want admins to be able to see all the voucher codes.
                    if (hasPermission(sender, "admin")) this.crazyManager.getVoucherCodes().forEach(voucherCode -> completions.add(voucherCode.getCode()));
                }
                case "open" -> {
                    if (hasPermission(sender, "admin"))
                        for (int i = 1; i <= voucherMenuListener.getMaxPage(); i++) completions.add(i + "");
                }
                case "give", "giveall" -> {
                    if (hasPermission(sender, "admin")) this.crazyManager.getVouchers().forEach(voucher -> completions.add(voucher.getName()));
                }
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /voucher arg0 arg1
            switch (args[0].toLowerCase()) {
                case "give", "giveall" -> {
                    if (hasPermission(sender, "admin")) completions.addAll(Arrays.asList("1", "2", "3", "4", "5", "10", "32", "64"));
                }
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        } else if (args.length == 4) { // /voucher arg0 arg1 arg2
            if (args[0].equalsIgnoreCase("give")) {
                if (hasPermission(sender, "admin")) this.plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }

            return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("voucher." + node) || sender.hasPermission("voucher.admin");
    }
}