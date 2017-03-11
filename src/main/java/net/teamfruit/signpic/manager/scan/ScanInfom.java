package net.teamfruit.signpic.manager.scan;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.teamfruit.signpic.manager.SignPictureManager;

public class ScanInfom extends BukkitRunnable {

	private final SignPictureManager plugin;
	private final Scanner scanner;
	private final CommandSender sender;
	private final long startTime;

	public ScanInfom(final SignPictureManager plugin, final Scanner scanner, final CommandSender sender) {
		this.plugin = plugin;
		this.scanner = scanner;
		this.sender = sender;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		final String elapsed = DurationFormatUtils.formatPeriod(this.startTime, System.currentTimeMillis(), "HH:mm:ss");
		switch (this.scanner.getState()) {
			case GETCHUNKCOORDS:
				this.sender.sendMessage(this.plugin.getI18n().format("chat.scan.stats", elapsed, this.scanner.getState(), 0, 0));
				break;
			case SCANNING:
			case PAUSE:
				this.sender.sendMessage(this.plugin.getI18n().format("chat.scan.stats", elapsed, this.scanner.getState(), this.scanner.getCompleteChunkCount(), this.scanner.getQueue().size()));
				break;
			case DONE:
				this.sender.sendMessage(this.plugin.getI18n().format("chat.scan.stats", elapsed, this.scanner.getState(), 0, 0));
				cancel();
			default:
				return;
		}
	}

}
