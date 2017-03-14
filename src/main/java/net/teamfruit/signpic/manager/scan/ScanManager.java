package net.teamfruit.signpic.manager.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import net.teamfruit.signpic.manager.Log;
import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.scan.Scanner.ScanData;
import net.teamfruit.signpic.manager.scan.Scanner.ScanState;

public class ScanManager {

	private final SignPictureManager plugin;
	private final Log logger;

	private Queue<ScanTask> queue = Queues.newArrayDeque();
	private @Nullable ScanTask current;

	public ScanManager(final SignPictureManager plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLog();
	}

	public Scanner scan(final Scanner scanner, final int speed) {
		return scan(new ScanTask(scanner, speed));
	}

	private Scanner scan(final ScanTask task) {
		final Scanner scanner = task.getScanner(this.plugin);
		if (isScanning())
			this.queue.add(task);
		else {
			scanner.runTaskTimer(this.plugin, 0, 20);
			this.current = task;
		}
		return scanner;
	}

	public boolean isScanning() {
		return this.current!=null;
	}

	public boolean isPause() {
		if (this.current!=null) {
			final Scanner scanner = this.current.getScanner();
			if (scanner!=null)
				return scanner.getState()==ScanState.PAUSE;
		}
		return false;
	}

	public @Nullable Scanner getCurrentScanner() {
		if (this.current!=null)
			return this.current.getScanner(this.plugin);
		return null;
	}

	public @Nullable ScanTask getCurrentScanTask() {
		return this.current;
	}

	public Queue<ScanTask> getQueue() {
		return this.queue;
	}

	public void next() {
		if (this.current!=null) {
			final Scanner scanner = this.current.getScanner();
			if (scanner!=null) {
				if (scanner.getState()==ScanState.DONE) {
					this.current = null;
					final ScanTask task = this.queue.poll();
					if (task!=null)
						scan(task);
				}
			}
		}
	}

	public @Nullable ScanTask pause() {
		if (this.current!=null)
			this.current.getScanner(this.plugin).pause();
		return this.current;
	}

	public @Nullable ScanTask resume() {
		if (this.current!=null) {
			final Scanner scanner = this.current.getScanner(this.plugin);
			if (scanner.getState()==ScanState.PAUSE)
				scanner.resume();
		}
		return this.current;
	}

	public @Nullable ScanTask stop() {
		if (this.current!=null)
			this.current.getScanner(this.plugin).stop();
		this.current = null;
		return this.current;
	}

	public @Nullable ScanTask setSpeed(final int speed) {
		if (this.current!=null)
			this.current.getScanner(this.plugin).setSpeed(speed);
		return this.current;
	}

	public void onEnable() {
		final File cache = new File(this.plugin.getDataFolder(), "scancache.ser");
		if (cache.exists()) {
			BukkitObjectInputStream bois = null;
			try {
				bois = new BukkitObjectInputStream(new FileInputStream(cache));
				final ScanTask task = (ScanTask) bois.readObject();
				scan(task);
				this.logger.info("Scan of SignPicture restarted.");
			} catch (final IOException e) {
				this.logger.warning("Failed to read the data of the last interrupted scan.", e);
			} catch (final ClassNotFoundException e) {
				this.logger.warning(e);
			} finally {
				try {
					if (bois!=null)
						bois.close();
				} catch (final IOException e) {
					this.logger.warning(e);
				}
				cache.delete();
			}
		}

		final File queue = new File(this.plugin.getDataFolder(), "scanqueue.ser");
		if (queue.exists()) {
			BukkitObjectInputStream bois = null;
			try {
				bois = new BukkitObjectInputStream(new FileInputStream(queue));
				this.queue = Queues.newArrayDeque((ArrayList) bois.readObject());
			} catch (final IOException e) {
				this.logger.warning("Failed to read the data of the scan added to the task.", e);
			} catch (final ClassNotFoundException e) {
				this.logger.warning(e);
			} finally {
				try {
					if (bois!=null)
						bois.close();
				} catch (final IOException e) {
					this.logger.warning(e);
				}
				queue.delete();
			}
		}

	}

	public void onDisable() {
		final ScanTask current = this.current;
		if (current!=null) {
			BukkitObjectOutputStream boos = null;
			try {
				boos = new BukkitObjectOutputStream(new FileOutputStream(new File(this.plugin.getDataFolder(), "scancache.ser")));
				current.setData(current.getScanner(this.plugin).getData());
				boos.writeObject(this.current);
			} catch (final FileNotFoundException e) {
				this.logger.warning("Failed to generate the data file of the scan being executed.", e);
			} catch (final IOException e) {
				this.logger.warning("Failed to save the data file of the scan being executed.", e);
			} finally {
				try {
					if (boos!=null)
						boos.close();
				} catch (final IOException e) {
					this.logger.warning(e);
				}
			}
		}

		if (this.queue.size()!=0) {
			BukkitObjectOutputStream boos = null;
			try {
				boos = new BukkitObjectOutputStream(new FileOutputStream(new File(this.plugin.getDataFolder(), "scanqueue.ser")));
				boos.writeObject(Lists.newArrayList(this.queue));
			} catch (final FileNotFoundException e) {
				this.logger.warning("Failed to generate the data file of the scan added to the queue.", e);
			} catch (final IOException e) {
				this.logger.warning("Failed to save the data file of the scan added to the queue.", e);
			} finally {
				try {
					if (boos!=null)
						boos.close();
				} catch (final IOException e) {
					this.logger.warning(e);
				}
			}
		}
	}

	public static class ScanTask implements Serializable {

		private static final long serialVersionUID = 7033533054979680626L;

		private transient final @Nullable Scanner scanner;
		private final String worldName;
		private final int speed;
		private @Nullable ScanData data;

		public ScanTask(final Scanner scanner, final int speed) {
			this.scanner = scanner;
			this.worldName = scanner.getWorld().getName();
			this.speed = speed;
		}

		public Scanner getScanner(final SignPictureManager plugin) {
			if (this.scanner!=null)
				return this.scanner;
			if (this.data!=null)
				return new Scanner(plugin, this.data);
			return new Scanner(plugin, Bukkit.getWorld(this.worldName), this.speed);
		}

		public @Nullable Scanner getScanner() {
			return this.scanner;
		}

		public String getWorldName() {
			return this.worldName;
		}

		public int getSpeed() {
			return this.speed;
		}

		public void setData(final ScanData data) {
			this.data = data;
		}
	}
}
