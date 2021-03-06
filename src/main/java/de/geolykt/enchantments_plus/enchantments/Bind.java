package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.ALL;

import java.util.ArrayList;
import java.util.List;

public class Bind extends CustomEnchantment {

    public static final int ID = 4;

    @Override
    public Builder<Bind> defaults() {
        return new Builder<>(Bind::new, ID)
            .maxLevel(1)
            .loreName("Bind")
            .probability(0)
            .enchantable(new Tool[]{ALL})
            .conflicting()
            .description("Keeps items with this enchantment in your inventory after death")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.BIND);
    }

    @Override
    public boolean onPlayerDeath(final PlayerDeathEvent evt, int level, boolean usedHand) {
        if (evt.getKeepInventory()) {
            return false;
        }
        final Player player = evt.getEntity();
        Config config = Config.get(player.getWorld());
        final ItemStack[] contents = player.getInventory().getContents().clone();
        final List<ItemStack> removed = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            if (!CustomEnchantment.getEnchants(contents[i], config.getWorld()).containsKey(this)) {
                contents[i] = null;
            } else {
                removed.add(contents[i]);
                evt.getDrops().remove(contents[i]);
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
            if (evt.getKeepInventory()) {
                evt.getDrops().addAll(removed);
            } else {
                player.getInventory().setContents(contents);
            }
        }, 1);
        return true;
    }
}