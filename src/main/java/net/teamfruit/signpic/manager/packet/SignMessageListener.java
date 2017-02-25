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
			if (player!=null)
				if (player.hasPermission("signpic.command.open")) {
					this.logger.info("recv");
					final SignPicturePacket packet = SignPictureManager.gson.fromJson(new String(message), SignPicturePacket.class);
					if (packet!=null) {
						if (StringUtils.equals(packet.command, "data")) {
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
						}
					}
				}
		}
	}

}
