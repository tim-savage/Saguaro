package com.winterhaven_mc.saguaro.commands;

import java.io.IOException;

import com.winterhaven_mc.saguaro.tasks.FileWriter;
import com.winterhaven_mc.saguaro.PluginMain;
import com.winterhaven_mc.saguaro.TelnetServer;
import com.winterhaven_mc.saguaro.tasks.TpsMeter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;


public class CommandManager implements CommandExecutor {

	// reference to main class
	private PluginMain plugin;

	public CommandManager(PluginMain plugin) {
		this.plugin = plugin;
		plugin.getCommand("saguaro").setExecutor(this);
	}


	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		int maxArgs = 1;
		if (args.length > maxArgs) {
			sender.sendMessage("Too many arguments!");
			return false;
		}

		// if called with no arguments, output config settings
		if (args.length < 1) {
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Version " + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Telnet enabled: " + plugin.getConfig().getString("telnet-enabled"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Listening Port: " + plugin.getConfig().getInt("telnet-port"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] File Output Enabled: " + plugin.getConfig().getString("file-output-enabled"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] File Update Period: " + plugin.getConfig().getInt("file-update-period"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] TPS Sample Period: " + plugin.getConfig().getInt("tps-sample-period"));
			return true;
		}
		
		String subCommand = args[0];
		
		// reload command
		if (subCommand.equalsIgnoreCase("reload") && sender.hasPermission("saguaro.reload")) {

			// reload config.yml
			plugin.reloadConfig();
			
			// send reloaded message to command sender
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] config reloaded.");
			
			// stop telnet server if it is running
			if (plugin.telnetServer != null) {
				plugin.telnetServer.stop();
			}
			
			// restart telnet server if configured
			if (plugin.getConfig().getBoolean("telnet-enabled",true)) {

				// read port from config file
				final int telnetPort = plugin.getConfig().getInt("telnet-port");

				// delay start of telnet server 2 seconds (40 ticks) to allow old server instance to close
				new BukkitRunnable() {

					@Override
					public void run() {

					// try to start telnet server on configured port
					try {
						plugin.telnetServer = new TelnetServer(plugin,telnetPort);
						sender.sendMessage(ChatColor.AQUA + "Telnet server listening on port "
								+ ChatColor.DARK_AQUA + telnetPort + ChatColor.AQUA + ".");
					} catch (IOException e) {
						sender.sendMessage(ChatColor.DARK_RED + "Could not start telnet server!");
						if (e.getLocalizedMessage() != null) {
							sender.sendMessage(e.getLocalizedMessage());
						}
						if (plugin.debug) {
							e.printStackTrace();
						}
					}
					}
				}.runTaskLater(plugin, 40);
			}
			
			// cancel file writer task if it is running
			if (plugin.fileWriterTask != null) {
				plugin.fileWriterTask.cancel();
			}
			
			// start new file writer task if configured
			if (plugin.getConfig().getBoolean("file-output-enabled")) {
				plugin.fileWriterTask = new FileWriter(plugin).runTaskTimer(plugin, 0,
						plugin.getConfig().getInt("file-update-period") * 20);
			}
			
			// restart TpsMeter
			if (plugin.tpsMeterTask != null) {
				plugin.tpsMeterTask.cancel();
			}
			plugin.tpsMeterTask = new TpsMeter(plugin).runTaskTimer(plugin,
					plugin.getConfig().getInt("tps-sample-period") * 20,
					plugin.getConfig().getInt("tps-sample-period") * 20);
			
			return true;
		}
		return false;
	}
	
}
