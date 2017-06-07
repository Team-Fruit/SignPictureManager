package net.teamfruit.signpic.manager.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.teamfruit.signpic.manager.SignPictureManager;

public class ReloadCommand extends PluginCommand {

	public ReloadCommand(final SignPictureManager plugin) {
		super(plugin, "reload");
		setPermission("signpic.command.reload");
	}

	@Override
	public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		this.plugin.onDisable();
		this.plugin.onLoad();
		this.plugin.onEnable();
		sender.sendMessage("Reload completed");
		return true;
	}
}
