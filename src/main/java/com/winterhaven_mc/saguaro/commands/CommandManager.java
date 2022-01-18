package com.winterhaven_mc.saguaro.commands;

import com.winterhaven_mc.saguaro.PluginMain;
import com.winterhaven_mc.saguaro.TelnetServer;
import com.winterhaven_mc.saguaro.tasks.FileWriter;
import com.winterhaven_mc.saguaro.tasks.TpsMeter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;


public class CommandManager implements CommandExecutor {

	// reference to main class
	private final PluginMain plugin;

	public CommandManager(final PluginMain plugin) {
		this.plugin = plugin;
		Objects.requireNonNull(plugin.getCommand("saguaro")).setExecutor(this);
	}


	@Override
	public boolean onCommand(final @Nonnull CommandSender sender,
	                         final @Nonnull Command cmd,
	                         final @Nonnull String label,
	                         final String[] args) {

		int maxArgs = 1;
		if (args.length > maxArgs) {
			sender.sendMessage("Too many arguments!");
			return false;
		}

		// if called with no arguments, return false to display bukkit command usage
		if (args.length < 1) {
			return false;
		}

		// get subcommand
		String subCommand = args[0];

		// reload command
		if (subCommand.equalsIgnoreCase("reload")) {
			return reloadCommand(sender);
		}

		// status command
		if (subCommand.equalsIgnoreCase("status")) {
			return statusCommand(sender);
		}

		return false;
	}


	/**
	 * Reload configuration
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent usage message
	 */
	private boolean reloadCommand(final CommandSender sender) {

		// check that sender has permission for reload command
		if (!sender.hasPermission("saguaro.reload")) {
			return true;
		}

		// re-install config file if necessary
		plugin.saveDefaultConfig();

		// reload config file
		plugin.reloadConfig();

		// set debug field
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// send reloaded message to command sender
		sender.sendMessage(ChatColor.AQUA + "[Saguaro] config reloaded.");

		// stop telnet server if it is running
		if (plugin.telnetServer != null) {
			plugin.telnetServer.stop();
		}

		// restart telnet server if configured
		if (plugin.getConfig().getBoolean("telnet-enabled", true)) {

			// read port from config file
			final int telnetPort = plugin.getConfig().getInt("telnet-port");

			// delay start of telnet server 2 seconds (40 ticks) to allow old server instance to close
			new BukkitRunnable() {

				@Override
				public void run() {

					// try to start telnet server on configured port
					try {
						plugin.telnetServer = new TelnetServer(plugin, telnetPort);
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
					plugin.getConfig().getLong("file-update-period") * 20L);
		}

		// cancel TpsMeter task if it is running
		if (plugin.tpsMeterTask != null) {
			plugin.tpsMeterTask.cancel();
		}

		// start new TpsMeter task
		plugin.tpsMeterTask = new TpsMeter(plugin).runTaskTimer(plugin,
				plugin.getConfig().getLong("tps-sample-period") * 20L,
				plugin.getConfig().getLong("tps-sample-period") * 20L);

		return true;
	}


	/**
	 * Display plugin status
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent usage message
	 */
	private boolean statusCommand(final CommandSender sender) {

		// check that sender has permission for status command
		if (!sender.hasPermission("saguaro.status")) {
			return false;
		}

		// output plugin name and version
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.AQUA + "Version: " + ChatColor.RESET + plugin.getDescription().getVersion());

		// output debug setting if set true
		if (plugin.debug) {
			sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
					+ ChatColor.GREEN + "DEBUG: "
					+ ChatColor.DARK_RED + "true");
		}

		// output telnet server enabled setting
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.GREEN + "Telnet enabled: "
				+ ChatColor.RESET + plugin.getConfig().getString("telnet-enabled"));

		// output telnet port setting
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.GREEN + "Listening Port: "
				+ ChatColor.RESET + plugin.getConfig().getInt("telnet-port"));

		// output file output enabled setting
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.GREEN + "File Output Enabled: "
				+ ChatColor.RESET + plugin.getConfig().getString("file-output-enabled"));

		// output file update interval setting
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.GREEN + "File Update Period: "
				+ ChatColor.RESET + plugin.getConfig().getLong("file-update-period"));

		// output tps sample period setting
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] "
				+ ChatColor.GREEN + "TPS Sample Period: "
				+ ChatColor.RESET + plugin.getConfig().getLong("tps-sample-period"));

		return true;
	}

}