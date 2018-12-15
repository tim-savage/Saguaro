package com.winterhaven_mc.saguaro.tasks;

import java.util.concurrent.TimeUnit;

import com.winterhaven_mc.saguaro.PluginMain;
import org.bukkit.scheduler.BukkitRunnable;


public class TpsMeter extends BukkitRunnable {

	// ticks per second
	public static double tps;

	// sample end time
	private long nanoEndTime;

	// sample period
	private long nanoSamplePeriod;
	

	/**
	 * Class constructor method
	 * @param plugin reference to plugin main class
	 */
	public TpsMeter(final PluginMain plugin) {

		// initalize nanoEndTime
		this.nanoEndTime = System.nanoTime();

		// get tps sample period from config
		this.nanoSamplePeriod = TimeUnit.SECONDS.toNanos(plugin.getConfig().getInt("tps-sample-period"));
	}


	@Override
	public void run() {

		long nanoStartTime = nanoEndTime;
		nanoEndTime = System.nanoTime();
		double nanoElapsedTime = nanoEndTime - nanoStartTime;

		tps = (double)nanoSamplePeriod / nanoElapsedTime * 20.0d;
	}

}
