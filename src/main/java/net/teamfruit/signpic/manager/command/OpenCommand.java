package net.teamfruit.signpic.manager.command;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.database.SignData;
import net.teamfruit.signpic.manager.database.SignDataBase;
import net.teamfruit.signpic.manager.packet.SignPicturePacket;

public class OpenCommand extends SignPicCommand {
	private final Logger logger;

	public OpenCommand(final SignPictureManager plugin) {
		super(plugin, "open");
		this.logger = this.plugin.getLogger();
	}

	@Override
	public boolean onSubCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof Player) {
			final SignDataBase db = this.plugin.signdata;
			if (db!=null) {
				final Player player = (Player) sender;
				final String token = sender.getName();
				final List<SignData> list = db.getSigns();
				this.plugin.tokendata.put(token, list);
				this.logger.info("open");
				player.sendPluginMessage(this.plugin, "signpic.list", SignPictureManager.gson.toJson(new SignPicturePacket("open", token, Integer.toString(list.size()))).getBytes());
				sender.sendMessage(db.getSigns().toString());
			}
		}
		return true;
	}
}
