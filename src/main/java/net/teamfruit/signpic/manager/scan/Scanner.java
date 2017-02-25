package net.teamfruit.signpic.manager.scan;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.kamesuta.mc.signpic.entry.EntryId;

import net.teamfruit.signpic.manager.SignPictureManager;

public class Scanner extends BukkitRunnable {

	private final SignPictureManager plugin;
	private final World world;

	private int speed;
	private ScanState state = ScanState.START;
	private @Nullable Queue<ChunkCoord> queue;
	private @Nullable ChunkCoord lastChunk;
	private @Nullable ScanState pauseState;
	private int completeChunkCount;

	public Scanner(final SignPictureManager plugin, final World world, final int speed) {
		this.plugin = plugin;
		this.world = world;
		this.speed = speed;
	}

	public Scanner(final SignPictureManager plugin, final ScanData data) {
		this.plugin = plugin;
		this.world = data.getWorld();
		this.state = data.state;
		this.queue = data.getQueue();
		this.lastChunk = data.lastChunk;
		this.speed = data.getSpeed();
	}

	public ScanState getState() {
		return this.state;
	}

	public World getWorld() {
		return this.world;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public void pause() {
		if (this.state!=ScanState.DONE) {
			this.pauseState = this.state;
			this.state = ScanState.PAUSE;
		}
	}

	public void stop() {
		this.state = ScanState.DONE;
	}

	public void resume() {
		if (this.pauseState!=null)
			this.state = this.pauseState;
		else
			throw new IllegalStateException("It is not possible to resume the running scan!");
		this.pauseState = null;
	}

	public ScanData getData() {
		return new ScanData(this);
	}

	public Queue<ChunkCoord> getQueue() {
		if (this.queue!=null)
			return this.queue;
		return this.queue = Queues.newArrayDeque();
	}

	public int getCompleteChunkCount() {
		return this.completeChunkCount;
	}

	@Override
	public void run() {
		final ScanManager scannerManager = this.plugin.scannerManager;
		if (scannerManager==null)
			return;
		if (this.state==ScanState.PAUSE)
			return;
		if (this.state==ScanState.DONE) {
			scannerManager.next();
			cancel();
		}

		if (this.state==ScanState.START&&this.queue==null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					final List<ChunkCoord> list = getAllChunks();
					Scanner.this.queue = Queues.newArrayDeque(list);
				}
			}.runTaskAsynchronously(this.plugin);
			this.state = ScanState.GETCHUNKCOORDS;
			return;
		}

		final Queue<ChunkCoord> queue = this.queue;
		if (queue!=null) {
			this.state = ScanState.SCANNING;

			for (int i = 0; i<this.speed; i++) {
				final ChunkCoord coord = queue.poll();
				if (coord==null) {
					this.state = ScanState.DONE;
					return;
				}
				this.lastChunk = coord;
				final ChunkCoord lastChunk = coord;

				final int bx = lastChunk.getX()<<4;
				final int bz = lastChunk.getZ()<<4;

				for (int xx = bx; xx<bx+16; xx++) {
					for (int zz = bz; zz<bz+16; zz++) {
						for (int yy = 0; yy<this.world.getMaxHeight(); yy++) {
							final Block b = this.world.getBlockAt(xx, yy, zz);
							if (b.getType()==Material.WALL_SIGN||b.getType()==Material.SIGN_POST) {
								final Sign sign = (Sign) b.getState();
								try {
									if (this.plugin.signdata!=null)
										this.plugin.signdata.setSign(b, null, EntryId.fromStrings(sign.getLines()));
								} catch (final Exception e) {
									this.plugin.getLogger().info(ExceptionUtils.getFullStackTrace(e));
								}
							}
						}
					}
				}
				this.completeChunkCount++;
			}
		}
	}

	private static Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");

	public List<ChunkCoord> getAllChunks() {
		final List<ChunkCoord> chunks = Lists.newArrayList();
		final File regionDir = new File(Bukkit.getWorldContainer(), this.world.getName()+"/region");

		final File[] regionFiles = regionDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final @Nullable File dir, final @Nullable String name) {
				if (dir==null||name==null)
					return false;
				return regionPattern.matcher(name).matches();
			}
		});

		for (final File f : regionFiles) {
			// extract coordinates from filename
			final Matcher matcher = regionPattern.matcher(f.getName());
			if (!matcher.matches())
				continue;

			final int mcaX = Integer.parseInt(matcher.group(1));
			final int mcaZ = Integer.parseInt(matcher.group(2));

			for (int cx = 0; cx<32; cx++)
				for (int cz = 0; cz<32; cz++)
					chunks.add(new ChunkCoord((mcaX<<5)+cx, (mcaZ<<5)+cz));
		}
		return chunks;
	}

	public static class ChunkCoord implements Serializable {

		private static final long serialVersionUID = 8049946647632634303L;

		private final int x;
		private final int z;

		public ChunkCoord(final int x, final int z) {
			this.x = x;
			this.z = z;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+this.x;
			result = prime*result+this.z;
			return result;
		}

		@Override
		public boolean equals(final @Nullable Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof ChunkCoord))
				return false;
			final ChunkCoord other = (ChunkCoord) obj;
			if (this.x!=other.x)
				return false;
			if (this.z!=other.z)
				return false;
			return true;
		}

		public int getX() {
			return this.x;
		}

		public int getZ() {
			return this.z;
		}

		public Chunk toChunk(final World world) {
			return world.getChunkAt(this.x, this.z);
		}
	}

	public static enum ScanState {
		START,
		GETCHUNKCOORDS,
		SCANNING,
		PAUSE,
		DONE;
	}

	public static class ScanData implements Serializable {

		private static final long serialVersionUID = -3129684795430808184L;

		public final ScanState state;
		public final String worldName;
		public final ArrayList<ChunkCoord> queue;
		public final @Nullable ChunkCoord lastChunk;
		public final int speed;

		public ScanData(final Scanner scanner) {
			this.state = scanner.state;
			this.worldName = scanner.getWorld().getName();
			this.queue = Lists.newArrayList(scanner.queue);
			this.lastChunk = scanner.lastChunk;
			this.speed = scanner.speed;
		}

		public World getWorld() {
			return Bukkit.getWorld(this.worldName);
		}

		public Queue<ChunkCoord> getQueue() {
			return Queues.newArrayDeque(this.queue);
		}

		public int getSpeed() {
			return this.speed;
		}
	}
}
