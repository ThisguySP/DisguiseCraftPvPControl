package org.minecraftsmp.dcpvpc;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.Listener;
import org.bukkit.event.HandlerList;
import org.bukkit.event.EventHandler;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

import pgDev.bukkit.DisguiseCraft.api.PlayerDisguiseEvent;

public class DisguiseCraftPvPControl extends JavaPlugin implements Listener {
	private boolean out;
	private boolean in;
	private boolean popOnFly;
	private boolean dropOnFly;
	DisguiseCraftAPI dcAPI;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Registered PvP damage listeners.");
		if (!(Files.exists(Paths.get("plugins/DisguiseCraftPvPControl/config.yml")))) {
			this.saveDefaultConfig();
			getLogger().info("Generated fresh configuration file.");
		}
		in = this.getConfig().getBoolean("undisguise.pvpin");
		out = this.getConfig().getBoolean("undisguise.pvpout");
		popOnFly = this.getConfig().getBoolean("undisguise.onfly");
		dropOnFly = this.getConfig().getBoolean("undisguise.droponfly");
		dcAPI = DisguiseCraft.getAPI();
	}
	
	public void onDisable() {
		HandlerList.unregisterAll((Listener)this);
		getLogger().info("Unregistered all listeners.");
	}
	
	
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (
			(event.getDamager() instanceof Player) &&
			(dcAPI.isDisguised((Player)event.getDamager())) &&
			out
		) {
			dcAPI.undisguisePlayer((Player)event.getDamager());
			getLogger().info(((Player)event.getDamager()).getName() + " attacked something and was undisguised.");
			((Player)event.getDamager()).sendRawMessage("§cYour disguise was broken!");
		} else if (
			(event.getEntity() instanceof Player) &&
			(dcAPI.isDisguised((Player)event.getEntity())) &&
			in
		) {
			dcAPI.undisguisePlayer((Player)event.getEntity());
			getLogger().info(((Player)event.getEntity()).getName() + " was attacked and undisguised.");
			((Player)event.getEntity()).sendRawMessage("§cYour disguise was broken!");
		} else {
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDisguise(PlayerDisguiseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if (((Player)event.getPlayer()).getAllowFlight()) {
			if (dropOnFly) {
				((Player)event.getPlayer()).setAllowFlight(false);
				((Player)event.getPlayer()).sendRawMessage("§cYou're now too heavy to fly!");
			} else if (popOnFly) {
				event.setCancelled(true);
				((Player)event.getPlayer()).sendRawMessage("§cYou cannot do that now.");
			}
		}
	}
	
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if (popOnFly && dcAPI.isDisguised((Player)event.getPlayer())) {
			dcAPI.undisguisePlayer((Player)event.getPlayer());
			((Player)event.getPlayer()).sendRawMessage("§cYour disguise was broken!");
		}
	}
}
