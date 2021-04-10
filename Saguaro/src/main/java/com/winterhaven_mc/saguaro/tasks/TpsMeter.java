package com.winterhaven_mc.saguaro.tasks;

import com.winterhaven_mc.saguaro.PluginMain;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;


public class TpsMeter extends BukkitRunnable {

	// ticks per second
	private static double tps = 0.0;

	// sample end time
	private long nanoEndTime;

	// sample period
	private final long nanoSamplePeriod;


	/**
	 * Class constructor method
	 *
	 * @param plugin reference to plugin main class
	 */
	public TpsMeter(final PluginMain plugin) {

		// initialize nanoEndTime
		this.nanoEndTime = System.nanoTime();

		// get tps sample period from config
		this.nanoSamplePeriod = TimeUnit.SECONDS.toNanos(plugin.getConfig().getInt("tps-sample-period"));
	}


	@Override
	public void run() {

		long nanoStartTime = nanoEndTime;
		nanoEndTime = System.nanoTime();
		double nanoElapsedTime = nanoEndTime - nanoStartTime;

		tps = (double) nanoSamplePeriod / nanoElapsedTime * 20.0d;
	}


	public static double getTps() {
		return tps;
	}

}
