package com.winterhaven_mc.saguaro;

import java.io.File;

import com.winterhaven_mc.saguaro.tasks.TpsMeter;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class DataCache {

	private long serverStartTime;

	private double tps = 0;
	private long uptime = 0;
	private int playerCount = 0;
	private int playerMax = 0;
	private int pluginCount = 0;
	private int entityCount = 0;
	private int chunkCount = 0;
	private long totalWorldSize = 0;
	private long memoryMax = 0;
	private long memoryTotal = 0;
	private long memoryFree = 0;

	/**
	 * Class constructor method
	 * @param plugin reference to plugin main class
	 */
	DataCache(final PluginMain plugin) {

		this.serverStartTime = System.currentTimeMillis();

		// Create the task anonymously and schedule to run every 30 seconds (600 ticks)
		new BukkitRunnable() {

			@Override
			public void run() {

				int entityCount = 0;
				int chunkCount = 0;
				long totalWorldSize = 0;

				for (World world : plugin.getServer().getWorlds()) {
					entityCount += world.getEntities().size();
					chunkCount += world.getLoadedChunks().length;
					totalWorldSize += FileUtils.sizeOfDirectory(new File(world
							.getWorldFolder().getAbsolutePath()));
				}

				setEntityCount(entityCount);
				setChunkCount(chunkCount);
				setTotalWorldSize(totalWorldSize);
				setPluginCount(plugin.getServer().getPluginManager().getPlugins().length);
				setPlayerCount(plugin.getServer().getOnlinePlayers().size());
				setPlayerMax(plugin.getServer().getMaxPlayers());
				setMemoryMax(Runtime.getRuntime().maxMemory());
				setMemoryTotal(Runtime.getRuntime().totalMemory());
				setMemoryFree(Runtime.getRuntime().freeMemory());
				setUptime(System.currentTimeMillis() - serverStartTime);
				setTps(TpsMeter.tps);
			}
		}.runTaskTimerAsynchronously(plugin, 0, 600);
	}


	public synchronized String getDataString() {

		StringBuilder data = new StringBuilder();

		data.append("uptime:");
		data.append(getUptime());
		data.append(' ');

		data.append("tps:");
		if (getTps() < 0) {
			data.append("U");
		} else {
			data.append(getTps());
		}
		data.append(' ');

		data.append("playerCount:");
		data.append(getPlayerCount());
		data.append(' ');

		data.append("playerMax:");
		data.append(getPlayerMax());
		data.append(' ');

		data.append("memMax:");
		data.append(getMemoryMax());
		data.append(' ');

		data.append("memTotal:");
		data.append(getMemoryTotal());
		data.append(' ');

		data.append("memFree:");
		data.append(getMemoryFree());
		data.append(' ');

		data.append("pluginCount:");
		data.append(getPluginCount());
		data.append(' ');

		data.append("chunkCount:");
		data.append(getChunkCount());
		data.append(' ');

		data.append("entityCount:");
		data.append(getEntityCount());
		data.append(' ');

		data.append("worldSize:");
		data.append(getTotalWorldSize());
		data.append(' ');

		return data.toString().trim();
	}

	private double getTps() {
		return tps;
	}

	private void setTps(double tps) {
		this.tps = tps;
	}

	private long getUptime() {
		return uptime;
	}

	private void setUptime(long uptime) {
		this.uptime = uptime;
	}

	private int getPlayerCount() {
		return playerCount;
	}

	private void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	private int getPlayerMax() {
		return playerMax;
	}

	private void setPlayerMax(int playerMax) {
		this.playerMax = playerMax;
	}

	private int getPluginCount() {
		return pluginCount;
	}

	private void setPluginCount(int pluginCount) {
		this.pluginCount = pluginCount;
	}

	private int getEntityCount() {
		return entityCount;
	}

	private void setEntityCount(int entityCount) {
		this.entityCount = entityCount;
	}

	private int getChunkCount() {
		return chunkCount;
	}

	private void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	private long getTotalWorldSize() {
		return totalWorldSize;
	}

	private void setTotalWorldSize(long totalWorldSize) {
		this.totalWorldSize = totalWorldSize;
	}

	private long getMemoryMax() {
		return memoryMax;
	}

	private void setMemoryMax(long memoryMax) {
		this.memoryMax = memoryMax;
	}

	private long getMemoryTotal() {
		return memoryTotal;
	}

	private void setMemoryTotal(long memoryTotal) {
		this.memoryTotal = memoryTotal;
	}

	private long getMemoryFree() {
		return memoryFree;
	}

	private void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}

}
