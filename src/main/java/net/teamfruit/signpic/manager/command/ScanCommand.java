package net.teamfruit.signpic.manager.command;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.teamfruit.signpic.manager.I18n;
import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.scan.ScanInfom;
import net.teamfruit.signpic.manager.scan.ScanManager;
import net.teamfruit.signpic.manager.scan.ScanManager.ScanTask;
import net.teamfruit.signpic.manager.scan.Scanner;

public class ScanCommand extends SignPicCommand {

	public ScanCommand(final SignPictureManager plugin) {
		super(plugin, "scan");
		registerSubCommand(new StartCommand(plugin));
		registerSubCommand(new PauseCommand(plugin));
		registerSubCommand(new ResumeCommand(plugin));
		registerSubCommand(new CancelCommand(plugin));
		registerSubCommand(new SetSpeedCommand(plugin));
		registerSubCommand(new StatusCommand(plugin));
	}

	public static class StartCommand extends SignPicCommand {

		public StartCommand(final SignPictureManager plugin) {
			super(plugin, "start");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			World world = null;
			int speed = 60;
			if (sender instanceof Player) {
				if (args.length==0)
					world = ((Player) sender).getWorld();
				else if (args.length>=1)
					world = Bukkit.getWorld(args[0]);
				if (args.length>=2)
					speed = NumberUtils.toInt(args[1], speed);
			} else {
				if (args.length<1)
					return false;
				world = Bukkit.getWorld(args[0]);
				if (args.length>=2)
					speed = NumberUtils.toInt(args[1], speed);
			}
			if (world==null)
				return false;

			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null) {
				final Scanner scanner = scannerManager.scan(new Scanner(this.plugin, world, speed), speed);
				sender.sendMessage(i18n.format("command.scan.start.start", world.getName()));
				sender.sendMessage(i18n.format("command.scan.start.speed", speed));
				sender.sendMessage(i18n.listFormat("command.scan.start.helps"));
				new ScanInfom(this.plugin, scanner, sender).runTaskTimer(this.plugin, 200, 200);
			}
			return true;
		}

		@Override
		public List<String> onTabComplete(final @Nullable CommandSender sender, final @Nullable Command command, final @Nullable String label, final @Nullable String[] args) {
			final List<String> list = Lists.newArrayList();
			if (sender!=null&&command!=null&&label!=null&&args!=null) {
				if (args.length<=1) {
					for (final World world : Bukkit.getWorlds())
						if (args.length<1||(args.length>=1&&StringUtils.startsWithIgnoreCase(world.getName(), args[0])))
							list.add(world.getName());
				}
			}
			return list;
		}
	}

	public static class PauseCommand extends SignPicCommand {

		public PauseCommand(final SignPictureManager plugin) {
			super(plugin, "pause");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null)
				if (scannerManager.isScanning()) {
					if (!scannerManager.isPause()) {
						final ScanTask task = scannerManager.pause();
						if (task!=null)
							sender.sendMessage(i18n.format("command.scan.pause.pause", task.getWorldName()));
						sender.sendMessage(i18n.listFormat("command.scan.pause.helps"));
					} else
						sender.sendMessage(i18n.format("command.scan.pause.already"));
				} else
					sender.sendMessage(i18n.format("command.scan.notscanned"));
			return true;
		}
	}

	public static class ResumeCommand extends SignPicCommand {

		public ResumeCommand(final SignPictureManager plugin) {
			super(plugin, "resume");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null)
				if (scannerManager.isScanning()) {
					if (scannerManager.isPause()) {
						final ScanTask task = scannerManager.resume();
						if (task!=null)
							sender.sendMessage(i18n.format("command.scan.resume.resume", task.getWorldName()));
						sender.sendMessage(i18n.listFormat("command.scan.resume.helps"));
					} else
						sender.sendMessage(i18n.format("command.scan.resume.already"));
				} else
					sender.sendMessage(i18n.format("command.scan.notscanned"));
			return true;
		}
	}

	public static class CancelCommand extends SignPicCommand {

		public CancelCommand(final SignPictureManager plugin) {
			super(plugin, "cancel");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null)
				if (scannerManager.isScanning()) {
					final ScanTask task = scannerManager.stop();
					if (task!=null)
						sender.sendMessage(i18n.format("command.scan.cancel.cancel", task.getWorldName()));
				} else
					sender.sendMessage(i18n.format("command.scan.notscanned"));
			return true;
		}
	}

	public static class SetSpeedCommand extends SignPicCommand {

		public SetSpeedCommand(final SignPictureManager plugin) {
			super(plugin, "setspeed");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null)
				if (scannerManager.isScanning()) {
					if (args.length<1)
						return false;
					if (!NumberUtils.isNumber(args[0])) {
						sender.sendMessage(i18n.format("command.scan.speed.invalid"));
						return true;
					}
					final ScanTask task = scannerManager.setSpeed(NumberUtils.toInt(args[0]));
					sender.sendMessage(i18n.format("command.scan.speed.speed", args[0]));
				} else
					sender.sendMessage(i18n.format("command.scan.notscanned"));
			return true;
		}
	}

	public static class StatusCommand extends SignPicCommand {

		public StatusCommand(final SignPictureManager plugin) {
			super(plugin, "status");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			final I18n i18n = this.plugin.i18n;
			final ScanManager scannerManager = this.plugin.scannerManager;
			if (i18n!=null&&scannerManager!=null)

				if (scannerManager.isScanning()) {
					final Scanner scanner = scannerManager.getCurrentScanner();
					if (scanner!=null)
						sender.sendMessage(i18n.format("command.scan.status.status", scanner.getState(), scanner.getCompleteChunkCount(), scanner.getQueue().size()));
					if (scannerManager.getQueue().size()>0) {
						final StringBuilder sb = new StringBuilder();
						for (final ScanTask task : scannerManager.getQueue())
							sb.append(task.getWorldName()).append(", ");
						sender.sendMessage(i18n.format("command.scan.status.queue", sb));
					}
				} else
					sender.sendMessage(i18n.format("command.scan.notscanned"));
			return true;
		}
	}
}