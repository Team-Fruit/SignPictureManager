package net.teamfruit.signpic.manager.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.teamfruit.signpic.manager.Log;
import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.database.SignData;
import net.teamfruit.signpic.manager.packet.SignPicturePacket;

public class OpenCommand extends PluginCommand {
	private final Log logger;

	public OpenCommand(final SignPictureManager plugin) {
		super(plugin, "open");
		setPermission("signpic.manage.open");
		this.logger = this.plugin.getLog();
	}

	@Override
	public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		final List<SignData> list = this.plugin.getSignData().getSigns();
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			final String token = player.getName();
			this.plugin.tokendata.put(token, list);
			this.logger.fine("open");
			player.sendPluginMessage(this.plugin, "signpic.list", SignPictureManager.gson.toJson(new SignPicturePacket("open", token, Integer.toString(list.size()))).getBytes());
			sender.sendMessage(SignPictureManager.gson.toJson(list));
		}
		return true;
	}
}
