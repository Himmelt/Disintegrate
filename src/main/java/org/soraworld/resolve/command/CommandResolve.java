package org.soraworld.resolve.command;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.soraworld.resolve.config.RslConfig;
import org.soraworld.resolve.constant.Constant;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;

import static org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asCraftMirror;
import static org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy;

public class CommandResolve extends CommandViolet {
    public CommandResolve(String name, String perm, RslConfig config, Plugin plugin) {
        super(name, perm, config, plugin);
        addSub(new IICommand("open", Constant.PERM_USE, config, true) {
            public boolean execute(Player player, ArrayList<String> args) {
                Inventory inv = config.getResolver(player);
                if (inv != null) player.openInventory(inv);
                return true;
            }
        });
        addSub(new IICommand("set", Constant.PERM_ADMIN, config, true) {
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.size() == 1 && !args.get(0).isEmpty()) {
                    ItemStack stack = player.getItemInHand();
                    if (stack != null) {
                        net.minecraft.server.v1_7_R4.ItemStack item = asNMSCopy(stack);
                        if (item.tag == null) item.tag = new NBTTagCompound();
                        item.tag.setString("resolve_id", args.get(0));
                        player.setItemInHand(asCraftMirror(item));
                        config.send(player, "setIdSuccess", args.get(0));
                    } else config.send(player, "emptyHand");
                } else config.sendV(player, Violets.KEY_INVALID_ARG);
                return true;
            }
        });
    }
}
