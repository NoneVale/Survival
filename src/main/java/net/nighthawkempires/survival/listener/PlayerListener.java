package net.nighthawkempires.survival.listener;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.util.ItemUtil;
import net.nighthawkempires.survival.SurvivalPlugin;
import net.nighthawkempires.survival.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

import static net.md_5.bungee.api.ChatColor.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UserModel userModel = SurvivalPlugin.getUserRegistry().getUser(player.getUniqueId());

        if (player.getKiller() != null) {
            userModel.addDeath();

            Player killer = player.getKiller();
            ItemStack itemStack = killer.getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                int beheadChance = 0;
                switch (itemStack.getType()) {
                    case WOODEN_SWORD: beheadChance = 15; break;
                    case STONE_SWORD: beheadChance = 25; break;
                    case IRON_SWORD: beheadChance = 35; break;
                    case GOLDEN_SWORD: beheadChance = 45; break;
                    case DIAMOND_SWORD: beheadChance = 65; break;
                    case NETHERITE_SWORD: beheadChance = 75; break;
                    default: beheadChance = 7; break;
                }

                Random random = new Random();
                if (random.nextInt(100) + 1 <= beheadChance) {
                    if (itemStack.getItemMeta().hasDisplayName()) {
                        String item = getItemStackInfo(itemStack);

                        String message = CorePlugin.getMessages().getChatMessage(GREEN + player.getName() + GRAY
                                + " has been been decapitated by " + GREEN + killer.getName() + GRAY + " using ");
                        TextComponent component = new TextComponent(message);
                        BaseComponent[] itemComponent = new BaseComponent[] {
                                new TextComponent(item)
                        };
                        TextComponent itemName = new TextComponent(itemStack.getItemMeta().getDisplayName());
                        itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemComponent));
                        component.addExtra(itemName);
                        component.addExtra(".");
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.spigot().sendMessage(component);
                        }
                        player.getWorld().dropItemNaturally(player.getLocation(), ItemUtil.getPlayerHead(
                                ChatColor.GREEN + player.getName() + "'s " + ChatColor.GRAY + "Skull", player.getName()));
                    } else {
                        String message = CorePlugin.getMessages().getChatMessage(GREEN + player.getName() + GRAY
                                + " has been been decapitated by " + GREEN + killer.getName());
                        TextComponent component = new TextComponent(message);
                        component.addExtra(".");
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.spigot().sendMessage(component);
                        }
                        player.getWorld().dropItemNaturally(player.getLocation(), ItemUtil.getPlayerHead(
                                ChatColor.GREEN + player.getName() + "'s " + ChatColor.GRAY + "Skull", player.getName()));
                    }
                }
            }

            userModel = SurvivalPlugin.getUserRegistry().getUser(killer.getUniqueId());
            userModel.addKill();
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled())return;

        Player player = event.getPlayer();
        if (player.hasPermission("ne.spawners.mine")) {
            if (event.getBlock().getType() == Material.SPAWNER) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    boolean isValidPickaxe = false;
                    switch (itemStack.getType()) {
                        case IRON_PICKAXE:
                        case GOLDEN_PICKAXE:
                        case DIAMOND_PICKAXE:
                        case NETHERITE_PICKAXE:
                            isValidPickaxe = true;
                    }

                    if (!isValidPickaxe) {
                        player.sendMessage(CorePlugin.getMessages().getChatMessage(ChatColor.GRAY + "You can only mine spawners with a iron, gold, diamond, or netherite pickaxe."));
                        event.setCancelled(true);
                        return;
                    }

                    if (!itemStack.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                        player.sendMessage(CorePlugin.getMessages().getChatMessage(ChatColor.GRAY + "Your pickaxe must have silk touch in order to mine this spawner."));
                        event.setCancelled(true);
                        return;
                    }


                    CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
                    String type;
                    switch (spawner.getSpawnedType()) {
                        case ZOMBIE:
                            type = ChatColor.DARK_GREEN + "Zombie";
                            break;
                        case SKELETON:
                            type = ChatColor.WHITE + "Skeleton";
                            break;
                        case PIG:
                            type = ChatColor.LIGHT_PURPLE + "Pig";
                            break;
                        case SPIDER:
                            type = ChatColor.DARK_PURPLE + "Spider";
                            break;
                        case CAVE_SPIDER:
                            type = ChatColor.DARK_PURPLE + "Cave Spider";
                            break;
                        case BLAZE:
                            type = ChatColor.GOLD + "Blaze";
                            break;
                        case SILVERFISH:
                            type = ChatColor.GRAY + "Silverfish";
                            break;
                        case HUSK:
                            type = ChatColor.YELLOW + "Husk";
                            break;
                        case ZOMBIE_VILLAGER:
                            type = ChatColor.DARK_GREEN + "Zombie Villager";
                            break;
                        default:
                            type = ChatColor.GRAY + spawner.getSpawnedType().getEntityClass().getName();
                            break;
                    }

                    ItemStack mobSpawner = new ItemStack(Material.SPAWNER);

                    ItemMeta itemMeta = mobSpawner.getItemMeta();
                    assert itemMeta != null;

                    itemMeta.setLore(Lists.newArrayList(type + ChatColor.GRAY + " Spawner"));
                    itemMeta.getPersistentDataContainer().set(SurvivalPlugin.CREATURE_KEY, PersistentDataType.STRING, spawner.getSpawnedType().name());

                    mobSpawner.setItemMeta(itemMeta);

                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), mobSpawner);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())return;

        Player player = event.getPlayer();

        ItemStack placedBlock = event.getItemInHand();

        if (placedBlock.getType() == Material.SPAWNER) {
            ItemMeta meta = placedBlock.getItemMeta();
            assert meta != null;
            if (placedBlock.getItemMeta().getPersistentDataContainer().has(SurvivalPlugin.CREATURE_KEY, PersistentDataType.STRING)) {
                EntityType entityType = EntityType.fromName(placedBlock.getItemMeta().getPersistentDataContainer().get(
                        SurvivalPlugin.CREATURE_KEY, PersistentDataType.STRING));

                Block block = event.getBlockPlaced();
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                assert entityType != null;
                spawner.setSpawnedType(entityType);
                spawner.update();
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem()) {
                if (CorePlugin.getMaterials().isSpawnEgg(event.getItem().getType())) {
                    Block block = event.getClickedBlock();
                    if (block.getType() == Material.SPAWNER) {
                        if (player.hasPermission("ne.spawners.change")) {
                            EntityType entityType = CorePlugin.getMaterials().getEntitySpawned(event.getItem().getType());

                            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                            creatureSpawner.setSpawnedType(entityType);
                            creatureSpawner.update();

                            if (player.getGameMode() != GameMode.CREATIVE) {
                                player.getInventory().getItemInMainHand().setAmount(event.getItem().getAmount() - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private String getItemStackInfo(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }
}
