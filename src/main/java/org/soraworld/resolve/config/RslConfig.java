package org.soraworld.resolve.config;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.soraworld.resolve.constant.Constant;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

import static org.bukkit.Material.STAINED_GLASS_PANE;
import static org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy;

public class RslConfig extends IIConfig {

    private final HashMap<UUID, Inventory> resolvers = new HashMap<>();
    private final HashMap<String, List<String>> outputs = new HashMap<>();

    private static final ItemStack HINT_PANE = new ItemStack(STAINED_GLASS_PANE, 1, (short) 2);
    private static final ItemStack BACK_PANE = new ItemStack(STAINED_GLASS_PANE, 1, (short) 3);
    private static final ItemStack RESOLVE_PANE = new ItemStack(STAINED_GLASS_PANE, 1, (short) 1);
    private static final ItemMeta HINT_META = HINT_PANE.getItemMeta();
    private static final ItemMeta RESOLVE_META = RESOLVE_PANE.getItemMeta();

    public RslConfig(File path, Plugin plugin) {
        super(path, plugin);
    }

    protected void loadOptions() {
        ConfigurationSection section = config_yaml.getConfigurationSection("recipes");
        for (String key : section.getKeys(false)) {
            outputs.put(key, new ArrayList<>(section.getStringList(key)));
        }
    }

    protected void saveOptions() {
        ConfigurationSection section = config_yaml.createSection("recipes");
        for (Map.Entry<String, List<String>> entry : outputs.entrySet()) {
            section.set(entry.getKey(), entry.getValue());
        }
    }

    public void afterLoad() {
    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.AQUA;
    }

    @Nonnull
    protected String defaultChatHead() {
        return "[" + Constant.PLUGIN_NAME + "] ";
    }

    public String defaultAdminPerm() {
        return Constant.PERM_ADMIN;
    }

    public Inventory getResolver(Player player) {
        if (player == null) return null;
        Inventory inv = resolvers.get(player.getUniqueId());
        if (inv == null) {
            inv = Bukkit.createInventory(player, InventoryType.CHEST, iiLang.format("title").replace('&', ChatColor.COLOR_CHAR));
            HINT_META.setDisplayName(iiLang.format("hint").replace('&', ChatColor.COLOR_CHAR));
            RESOLVE_META.setDisplayName(iiLang.format("click").replace('&', ChatColor.COLOR_CHAR));
            BACK_PANE.setItemMeta(HINT_META);
            HINT_PANE.setItemMeta(HINT_META);
            RESOLVE_PANE.setItemMeta(RESOLVE_META);
            for (int slot = 0; slot < inv.getSize(); slot++) {
                if (slot != 13) inv.setItem(slot, BACK_PANE);
            }
            inv.setItem(3, HINT_PANE);
            inv.setItem(4, HINT_PANE);
            inv.setItem(5, HINT_PANE);
            inv.setItem(12, HINT_PANE);
            inv.setItem(14, HINT_PANE);
            inv.setItem(21, HINT_PANE);
            inv.setItem(22, RESOLVE_PANE);
            inv.setItem(23, HINT_PANE);
            resolvers.put(player.getUniqueId(), inv);
        }
        return inv;
    }

    private void resolve(Player player, String id) {
        List<String> cmds = outputs.get(id);
        if (cmds != null && !cmds.isEmpty()) {
            for (String cmd : cmds) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
            }
        }
    }

    public void checkClick(Player player, int slot) {
        if (slot == 22) {
            Inventory inv = getResolver(player);
            ItemStack item = inv.getItem(13);
            if (item != null) {
                NBTTagCompound nbt = asNMSCopy(item).tag;
                if (nbt != null && nbt.hasKeyOfType("resolve_id", 8)) {
                    resolve(player, nbt.getString("resolve_id"));
                    inv.setItem(13, null);
                    send(player, "resolveSuccess");
                    return;
                }
            }
            send(player, "emptyOrCantResolve");
        }
    }
}
