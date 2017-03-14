package net.teamfruit.signpic.manager.command;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.google.common.collect.Lists;

import net.teamfruit.signpic.manager.SignPictureManager;

public class PluginCommand implements CommandExecutor, TabCompleter {

	protected final SignPictureManager plugin;
	private final List<PluginCommand> subCommands = Lists.newArrayList();
	private final String name;

	private @Nullable PluginCommand parent;
	private @Nullable String permission;

	public PluginCommand(final SignPictureManager plugin, final String name) {
		this.plugin = plugin;
		this.name = name;
	}

	public void registerSubCommand(final PluginCommand command) {
		command.setParent(this);
		this.subCommands.add(command);
	}

	public String getFullCommandName() {
		if (this.parent!=null)
			return this.parent.getName()+" "+this.name;
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public void setPermission(final String permission) {
		this.permission = permission;
	}

	public boolean hasPermission(final CommandSender sender) {
		if (this.permission!=null)
			return sender.hasPermission(this.permission);
		return true;
	}

	public void setParent(final PluginCommand command) {
		this.parent = command;
	}

	public boolean isRoot() {
		return this.parent==null;
	}

	@Override
	public boolean onCommand(final @Nullable CommandSender sender, final @Nullable Command command, final @Nullable String label, final @Nullable String[] args) {
		if (sender!=null&&command!=null&&label!=null&&args!=null) {
			final boolean b = onSubCommand(sender, command, label, args);
			final PluginCommand subCommand = getSubCommand(sender, command, label, args);
			if (subCommand!=null) {
				if (subCommand.hasPermission(sender))
					return subCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length))||b;
				if (command.getPermissionMessage()!=null)
					for (final String line : command.getPermissionMessage().replace("<permission>", subCommand.permission).split("\n"))
						sender.sendMessage(line);
				else
					return false;
				return true;
			}
			return b;
		}
		return true;
	}

	/**
	 * これをOverride
	 * @param paramCommandSender
	 * @param paramCommand
	 * @param paramString
	 * @param paramArrayOfString
	 * @return
	 */
	public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		return false;
	}

	@Override
	public List<String> onTabComplete(final @Nullable CommandSender sender, final @Nullable Command command, final @Nullable String label, final @Nullable String[] args) {
		final List<String> list = Lists.newArrayList();
		if (sender!=null&&command!=null&&label!=null&&args!=null) {
			if (args.length<=1) {
				for (final PluginCommand subCommand : this.subCommands)
					if (args.length<1||(args.length>=1&&StringUtils.startsWithIgnoreCase(subCommand.getName(), args[0])))
						list.add(subCommand.getName());
			} else {
				final PluginCommand subCommand = getSubCommand(sender, command, label, args);
				if (subCommand!=null&&subCommand.hasPermission(sender))
					return subCommand.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
			}
		}
		return list;
	}

	public @Nullable PluginCommand getSubCommand(final @Nullable CommandSender sender, final @Nullable Command command, final @Nullable String label, final @Nullable String[] args) {
		if (sender==null|command==null||label==null||args==null)
			return null;
		if (args.length>=1)
			for (final PluginCommand subCommand : this.subCommands)
				if (subCommand.getName().equalsIgnoreCase(args[0]))
					return subCommand;
		return null;
	}
}
