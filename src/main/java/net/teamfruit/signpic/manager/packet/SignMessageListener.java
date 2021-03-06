package net.teamfruit.signpic.manager.packet;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.kamesuta.mc.signpic.entry.EntryId;

import net.teamfruit.signpic.manager.Log;
import net.teamfruit.signpic.manager.SignPictureManager;
import net.teamfruit.signpic.manager.database.SignData;

public class SignMessageListener implements PluginMessageListener {
	private final SignPictureManager plugin;
	private final Log logger;

	public SignMessageListener(final SignPictureManager plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLog();
	}

	@Override
	public void onPluginMessageReceived(final @Nullable String channel, final @Nullable Player player, final @Nullable byte[] message) {
		if (StringUtils.equals(channel, "signpic.manager")) {
			if (player!=null&&message!=null) {
				this.logger.fine("recv");
				onPacket(player, SignPictureManager.gson.fromJson(new String(message), SignPicturePacket.class));
			}
		}
	}

	public void onPacket(final Player player, final SignPicturePacket packet) {
		if (StringUtils.equals(packet.command, "data")&&player.hasPermission("signpic.open")) {
			if (StringUtils.isNotEmpty(packet.token)&&NumberUtils.isNumber(packet.data)) {
				final int i = NumberUtils.toInt(packet.data);
				final List<SignData> datas = this.plugin.tokendata.get(packet.token);
				if (datas!=null) {
					if (0<=i&&i<datas.size()) {
						final SignData data = datas.get(i);
						this.logger.fine("send");
						player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("data", packet.data, data.toString())).getBytes());
					}
				}
			}
		} else if (StringUtils.equals(packet.command, "edit")&&player.hasPermission("signpic.manage.edit")) {
			if (NumberUtils.isNumber(packet.token)) {
				try {
					final SignData data = this.plugin.getSignData().getSign(Integer.parseInt(packet.token));
					final World world = Bukkit.getWorld(data.getWorldName());
					final Location location = data.getLocation();
					final Block block = world.getBlockAt(location);
					if (block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST) {
						final Sign sign = (Sign) block.getState();
						if (packet.data.length()>60) {
							player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("error", packet.token, "signpic.manager.error.signindexoutofbounds")).getBytes());
							return;
						}
						for (int i = 0; i<=3; i++)
							sign.setLine(i, StringUtils.substring(packet.data, i*15, (i+1)*15));
						data.setSign(packet.data);
						if (EntryId.from(packet.data).isValid()) {
							data.setSign(packet.data);
							this.plugin.getSignData().setSign(data);
						} else
							this.plugin.getSignData().removeSign(block);
						player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("accept", packet.token, "signpic.manager.accept")).getBytes());
					}
				} catch (final Exception e) {
					player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("error", packet.token, "signpic.manager.error.unknown")).getBytes());
				}
			}
		} else if (StringUtils.equals(packet.command, "remove")&&player.hasPermission("signpic.manage.remove")) {
			if (NumberUtils.isNumber(packet.token)) {
				try {
					final SignData data = this.plugin.getSignData().getSign(NumberUtils.toInt(packet.token));
					final World world = Bukkit.getWorld(data.getWorldName());
					final Location location = data.getLocation();
					final Block block = world.getBlockAt(location);
					if (block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST)
						block.setType(Material.AIR, BooleanUtils.toBoolean(packet.data));
					this.plugin.getSignData().removeSign(block);
					player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("accept", packet.token, "signpic.manager.accept")).getBytes());
				} catch (final Exception e) {
					player.sendPluginMessage(this.plugin, "signpic.manager", SignPictureManager.gson.toJson(new SignPicturePacket("error", packet.token, "signpic.manager.error.unknown")).getBytes());
				}
			}
		}
	}
}
