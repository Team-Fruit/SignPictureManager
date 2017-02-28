package net.teamfruit.signpic.manager.packet;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.database.SignData;

public class SignMessageListener implements PluginMessageListener {
	private final SignPictureManager plugin;
	private final Logger logger;

	public SignMessageListener(final SignPictureManager plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
	}

	@Override
	public void onPluginMessageReceived(final @Nullable String channel, final @Nullable Player player, final @Nullable byte[] message) {
		if (StringUtils.equals(channel, "signpic.list")) {
			if (player!=null&&message!=null) {
				this.logger.info("recv");
				onPacket(player, SignPictureManager.gson.fromJson(new String(message), SignPicturePacket.class));
			}
		}
	}

	public void onPacket(final Player player, final SignPicturePacket packet) {
		if (StringUtils.equals(packet.command, "data")&&player.hasPermission("signpic.command.open")) {
			if (StringUtils.isNotEmpty(packet.token)&&NumberUtils.isNumber(packet.data)) {
				final int i = NumberUtils.toInt(packet.data);
				final List<SignData> datas = this.plugin.tokendata.get(packet.token);
				if (datas!=null) {
					if (0<=i&&i<datas.size()) {
						final SignData data = datas.get(i);
						this.logger.info("send");
						player.sendPluginMessage(this.plugin, "signpic.list", SignPictureManager.gson.toJson(new SignPicturePacket("data", packet.data, data.toString())).getBytes());
					}
				}
			}
		} else if (StringUtils.equals(packet.command, "edit")) {
		} else if (StringUtils.equals(packet.command, "delete")) {
		}
	}
}
