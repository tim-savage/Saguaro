package com.winterhaven_mc.saguaro;

import com.winterhaven_mc.saguaro.tasks.TpsMeter;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;

import java.io.File;


public class Observation {

	private static long serverStartTime = System.currentTimeMillis();

	private final long timestamp;
	private final double tps;
	private final long uptime;
	private final int playerCount;
	private final int playerMax;
	private final int pluginCount;
	private final int entityCount;
	private final int chunkCount;
	private final long totalWorldSize;
	private final long memoryMax;
	private final long memoryTotal;
	private final long memoryFree;


	Observation (final PluginMain plugin) {

		// get current time stamp
		this.timestamp = System.currentTimeMillis();

		// get server tps
		this.tps = TpsMeter.getTps();

		// set server uptime
		this.uptime = timestamp - serverStartTime;

		// set server online player count
		this.playerCount = plugin.getServer().getOnlinePlayers().size();

		// set server player maximum setting
		this.playerMax = plugin.getServer().getMaxPlayers();

		// set server plugin count
		this.pluginCount = plugin.getServer().getPluginManager().getPlugins().length;

		// set server max memory
		this.memoryMax = Runtime.getRuntime().maxMemory();

		// set server total memory
		this.memoryTotal = Runtime.getRuntime().totalMemory();

		// set server free memory
		this.memoryFree = Runtime.getRuntime().freeMemory();

		// compute multi world metrics
		int tempEntityCount = 0;
		int tempChunkCount = 0;
		long tempTotalWorldSize = 0;

		for (World world : plugin.getServer().getWorlds()) {
			tempEntityCount += world.getEntities().size();
			tempChunkCount += world.getLoadedChunks().length;
			tempTotalWorldSize += FileUtils.sizeOfDirectory(new File(world
					.getWorldFolder().getAbsolutePath()));
		}

		this.entityCount = tempEntityCount;
		this.chunkCount = tempChunkCount;
		this.totalWorldSize = tempTotalWorldSize;

	}

	// get timestamp
	@SuppressWarnings("unused")
	public long getTimestamp() {
		return this.timestamp;
	}

	// get server uptime
	@SuppressWarnings("WeakerAccess")
	public long getUptime() {
		return this.uptime;
	}

	// get server tps
	@SuppressWarnings("WeakerAccess")
	public double getTps() {
		return this.tps;
	}

	// get server player count
	@SuppressWarnings("WeakerAccess")
	public int getPlayerCount() {
		return this.playerCount;
	}

	// get server player max
	@SuppressWarnings("WeakerAccess")
	public int getPlayerMax() {
		return this.playerMax;
	}

	// get server plugin count
	@SuppressWarnings("WeakerAccess")
	public int getPluginCount() {
		return this.pluginCount;
	}

	// get server entity count
	@SuppressWarnings("WeakerAccess")
	public int getEntityCount() {
		return this.entityCount;
	}

	// get server chunk count
	@SuppressWarnings("WeakerAccess")
	public int getChunkCount() {
		return this.chunkCount;
	}

	// get server total world size
	@SuppressWarnings("WeakerAccess")
	public long getTotalWorldSize() {
		return totalWorldSize;
	}

	// get server memory max size
	@SuppressWarnings("WeakerAccess")
	public long getMemoryMax() {
		return memoryMax;
	}

	// get server memory total size
	@SuppressWarnings("WeakerAccess")
	public long getMemoryTotal() {
		return memoryTotal;
	}

	// get server memory free size
	@SuppressWarnings("WeakerAccess")
	public long getMemoryFree() {
		return memoryFree;
	}

	// get cacti compatible string of values
	synchronized String getDataString() {

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

}
