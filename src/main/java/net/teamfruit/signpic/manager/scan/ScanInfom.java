package net.teamfruit.signpic.manager.scan;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.teamfruit.signpic.manager.I18n;
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
		final I18n i18n = this.plugin.i18n;
		final String elapsed = DurationFormatUtils.formatPeriod(this.startTime, System.currentTimeMillis(), "HH:mm:ss");
		if (i18n!=null)
			switch (this.scanner.getState()) {
				case GETCHUNKCOORDS:
					this.sender.sendMessage(i18n.format("chat.scan.status", elapsed, this.scanner.getState(), 0, 0));
					break;
				case SCANNING:
				case PAUSE:
					this.sender.sendMessage(i18n.format("chat.scan.status", elapsed, this.scanner.getState(), this.scanner.getCompleteChunkCount(), this.scanner.getQueue().size()));
					break;
				case DONE:
					this.sender.sendMessage(i18n.format("chat.scan.status", elapsed, this.scanner.getState(), 0, 0));
					cancel();
				default:
					return;
			}
	}

}
