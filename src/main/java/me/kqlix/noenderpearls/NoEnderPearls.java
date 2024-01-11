package me.kqlix.noenderpearls;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Set;
import java.util.stream.Collectors;

public final class NoEnderPearls extends JavaPlugin implements Listener {

    private Set<PotionEffectType> blackListedTypes;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigData();

        getCommand("noenderpearls-reload").setExecutor((sender, cmd, label, args) -> {
            try {
                reloadConfigData();
                sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
                return true;
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An error occurred during reload process: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        Projectile entity = event.getEntity();
        ProjectileSource source = entity.getShooter();

        if (entity.getType() != EntityType.ENDER_PEARL ||
                !(source instanceof Player player)) {
            return;
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (blackListedTypes.contains(effect.getType())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private void reloadConfigData() {
        reloadConfig();
        blackListedTypes = getConfig()
                .getStringList("blacklisted-effects")
                .stream()
                .map(PotionEffectType::getByName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
