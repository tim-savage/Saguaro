package com.winterhaven_mc.saguaro.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.winterhaven_mc.saguaro.PluginMain;
import org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class FileWriter extends BukkitRunnable {

	private final PluginMain plugin;
	private final String fileName;

	/**
	 * Class constructor method
	 *
	 * @param plugin reference to plugin main class
	 */
	public FileWriter(PluginMain plugin) {

		this.plugin = plugin;
		this.fileName = plugin.getDataFolder() + File.separator + "cacti_data.txt";
	}

	@Override
	public void run() {

		File dataFile = new File(fileName);
		try {
			FileUtils.writeStringToFile(dataFile, plugin.dataCache.getDataString(), Charset.defaultCharset());
		} catch (IOException e) {
			plugin.getLogger().severe("Could not write to file!");
			if (e.getLocalizedMessage() != null) {
				plugin.getLogger().severe(e.getLocalizedMessage());
			}
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}

}
