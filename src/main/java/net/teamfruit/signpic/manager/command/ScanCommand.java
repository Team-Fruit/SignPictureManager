package net.teamfruit.signpic.manager.command;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.scan.ScanInfom;
import net.teamfruit.signpic.manager.scan.ScanManager.ScanTask;
import net.teamfruit.signpic.manager.scan.Scanner;

public class ScanCommand extends PluginCommand {

	public ScanCommand(final SignPictureManager plugin) {
		super(plugin, "scan");
		setPermission("signpic.manage.scan");
		registerSubCommand(new StartCommand(plugin));
		registerSubCommand(new PauseCommand(plugin));
		registerSubCommand(new ResumeCommand(plugin));
		registerSubCommand(new CancelCommand(plugin));
		registerSubCommand(new SetSpeedCommand(plugin));
		registerSubCommand(new StatsCommand(plugin));
	}

	@Override
	public boolean onCommand(@Nullable final CommandSender sender, @Nullable final Command command, @Nullable final String label, @Nullable final String[] args) {
		if (!executeSubCommands(sender, command, label, args)&&sender!=null&&label!=null)
			sender.sendMessage("/"+label+" scan <subcommand>");
		return true;
	}

	public static class StartCommand extends PluginCommand {

		public StartCommand(final SignPictureManager plugin) {
			super(plugin, "start");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			World world = null;
			int speed = 1;
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
				throw new CommandException();

			final Scanner scanner = this.plugin.getScanManager().scan(new Scanner(this.plugin, world, speed), speed);
			sender.sendMessage(this.plugin.getI18n().format("command.scan.start.start", world.getName()));
			sender.sendMessage(this.plugin.getI18n().format("command.scan.start.speed", speed));
			sender.sendMessage(this.plugin.getI18n().listFormat("command.scan.start.helps"));
			new ScanInfom(this.plugin, scanner, sender).runTaskTimer(this.plugin, 200, 200);

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

	public static class PauseCommand extends PluginCommand {

		public PauseCommand(final SignPictureManager plugin) {
			super(plugin, "pause");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			if (this.plugin.getScanManager().isScanning()) {
				if (!this.plugin.getScanManager().isPause()) {
					final ScanTask task = this.plugin.getScanManager().pause();
					if (task!=null)
						sender.sendMessage(this.plugin.getI18n().format("command.scan.pause.pause"));
					sender.sendMessage(this.plugin.getI18n().listFormat("command.scan.pause.helps"));
				} else
					sender.sendMessage(this.plugin.getI18n().format("command.scan.pause.already"));
			} else
				sender.sendMessage(this.plugin.getI18n().format("command.scan.notscanned"));
			return true;
		}
	}

	public static class ResumeCommand extends PluginCommand {

		public ResumeCommand(final SignPictureManager plugin) {
			super(plugin, "resume");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			if (this.plugin.getScanManager().isScanning()) {
				if (this.plugin.getScanManager().isPause()) {
					final ScanTask task = this.plugin.getScanManager().resume();
					if (task!=null)
						sender.sendMessage(this.plugin.getI18n().format("command.scan.resume.resume"));
					sender.sendMessage(this.plugin.getI18n().listFormat("command.scan.resume.helps"));
				} else
					sender.sendMessage(this.plugin.getI18n().format("command.scan.resume.already"));
			} else
				sender.sendMessage(this.plugin.getI18n().format("command.scan.notscanned"));
			return true;
		}
	}

	public static class CancelCommand extends PluginCommand {

		public CancelCommand(final SignPictureManager plugin) {
			super(plugin, "cancel");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			if (this.plugin.getScanManager().isScanning()) {
				final ScanTask task = this.plugin.getScanManager().stop();
				if (task!=null)
					sender.sendMessage(this.plugin.getI18n().format("command.scan.cancel.cancel"));
			} else
				sender.sendMessage(this.plugin.getI18n().format("command.scan.notscanned"));
			return true;
		}
	}

	public static class SetSpeedCommand extends PluginCommand {

		public SetSpeedCommand(final SignPictureManager plugin) {
			super(plugin, "setspeed");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			if (this.plugin.getScanManager().isScanning()) {
				if (args.length<1)
					return false;
				if (!NumberUtils.isNumber(args[0])) {
					sender.sendMessage(this.plugin.getI18n().format("command.scan.speed.invalid"));
					return true;
				}
				this.plugin.getScanManager().setSpeed(NumberUtils.toInt(args[0]));
				sender.sendMessage(this.plugin.getI18n().format("command.scan.speed.speed", args[0]));
			} else
				sender.sendMessage(this.plugin.getI18n().format("command.scan.notscanned"));
			return true;
		}
	}

	public static class StatsCommand extends PluginCommand {

		public StatsCommand(final SignPictureManager plugin) {
			super(plugin, "stats");
		}

		@Override
		public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
			if (this.plugin.getScanManager().isScanning()) {
				final Scanner scanner = this.plugin.getScanManager().getCurrentScanner();
				if (scanner!=null)
					sender.sendMessage(this.plugin.getI18n().format("command.scan.stats.stats", scanner.getState(), scanner.getCompleteChunkCount(), scanner.getQueue().size()));
				if (this.plugin.getScanManager().getQueue().size()>0) {
					final StringBuilder sb = new StringBuilder();
					for (final ScanTask task : this.plugin.getScanManager().getQueue())
						sb.append(task.getWorldName()).append(", ");
					sender.sendMessage(this.plugin.getI18n().format("command.scan.stats.queue", sb));
				}
			} else
				sender.sendMessage(this.plugin.getI18n().format("command.scan.notscanned"));
			return true;
		}
	}
}
