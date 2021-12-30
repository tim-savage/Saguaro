package com.winterhaven_mc.saguaro;

import org.bukkit.scheduler.BukkitRunnable;


public class DataCache {

	private Observation currentData;


	/**
	 * Class constructor method
	 *
	 * @param plugin reference to plugin main class
	 */
	DataCache(final PluginMain plugin) {

		// Create the task anonymously and schedule to run every 30 seconds (600 ticks)
		new BukkitRunnable() {

			@Override
			public void run() {

				currentData = new Observation(plugin);
			}
		}.runTaskTimerAsynchronously(plugin, 0, 600);
	}


	public String getDataString() {

		return currentData.getDataString();

	}

}
