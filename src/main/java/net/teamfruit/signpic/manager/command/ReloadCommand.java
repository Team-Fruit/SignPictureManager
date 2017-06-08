package net.teamfruit.signpic.manager.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.teamfruit.signpic.manager.SignPictureManager;

public class ReloadCommand extends PluginCommand {

	public ReloadCommand(final SignPictureManager plugin) {
		super(plugin, "reload");
		setPermission("signpic.manage.reload");
	}

	@Override
	public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		this.plugin.getPluginLoader().disablePlugin(this.plugin);
		this.plugin.getPluginLoader().enablePlugin(this.plugin);
		sender.sendMessage("Reload completed");
		return true;
	}
}
