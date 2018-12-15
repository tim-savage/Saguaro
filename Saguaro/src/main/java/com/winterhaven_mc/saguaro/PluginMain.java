package com.winterhaven_mc.saguaro;

import java.io.IOException;

import com.winterhaven_mc.saguaro.commands.CommandManager;
import com.winterhaven_mc.saguaro.tasks.FileWriter;
import com.winterhaven_mc.saguaro.tasks.TpsMeter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public final class PluginMain extends JavaPlugin {

	public Boolean debug = getConfig().getBoolean("debug");

	public TelnetServer telnetServer;
	public DataCache dataCache;

	public BukkitTask tpsMeterTask;
	public BukkitTask fileWriterTask;


	@Override
	public void onEnable() {

		// copy default config from jar if it doesn't exist
		saveDefaultConfig();

		// instantiate command manager
		new CommandManager(this);

		// instantiate TPS Meter
		tpsMeterTask = new TpsMeter(this).runTaskTimer(this,
				getConfig().getInt("tps-sample-period") * 20,
				getConfig().getInt("tps-sample-period") * 20);

		// instantiate data cache
		dataCache = new DataCache(this);

		// if configured, start file writer task
		if (getConfig().getBoolean("file-output-enabled", false)) {
			fileWriterTask = new FileWriter(this).runTaskTimer(this, 0,
					getConfig().getInt("file-update-period") * 20);
		}

		// if configured, start telnet server
		if (getConfig().getBoolean("telnet-enabled")) {
			try {
				telnetServer = new TelnetServer(this, getConfig().getInt("telnet-port"));
				getLogger().info(
						"Telnet server listening on port " + getConfig().getInt("telnet-port"));
			} catch (IOException e) {
				getLogger().severe("Could not start telnet server!");
				if (e.getLocalizedMessage() != null) {
					getLogger().severe(e.getLocalizedMessage());
				}
				if (debug) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public void onDisable() {

		// stop telnet server
		if (telnetServer != null) {
			telnetServer.stop();
		}

		// stop file writer
		if (fileWriterTask != null) {
			fileWriterTask.cancel();
		}

		// stop tps meter
		if (tpsMeterTask != null) {
			tpsMeterTask.cancel();
		}

		// cancel all remaining repeating tasks for this plugin
		getServer().getScheduler().cancelTasks(this);
	}

}
