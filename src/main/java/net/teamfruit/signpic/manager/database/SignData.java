package net.teamfruit.signpic.manager.database;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.kamesuta.mc.signpic.entry.EntryId;

import net.teamfruit.signpic.manager.SignPictureManager;

@Entity
@Table(name = "sign")
public class SignData extends LocationData {

	@NotEmpty
	private @Nullable String sign;

	@NotNull
	private @Nullable String playerName;

	@NotNull
	private @Nullable String playerUUID;

	public @Nullable Player getPlayer() {
		if (this.playerName!=null)
			return Bukkit.getServer().getPlayer(this.playerName);
		return null;
	}

	@Override
	public String toString() {
		return SignPictureManager.gson.toJson(this);
	}

	public String getSign() {
		if (this.sign!=null)
			return this.sign;
		return this.sign = "";
	}

	public String getPlayerName() {
		if (this.playerName!=null)
			return this.playerName;
		return this.playerName = "";
	}

	public String getPlayerUUID() {
		if (this.playerUUID!=null)
			return this.playerUUID;
		return this.playerUUID = "";
	}

	public static SignData fromEvent(final Block block, final @Nullable Player player, final EntryId id) {
		final SignData data = new SignData();
		data.setWorldName(block.getWorld().getName());
		data.setX(block.getX());
		data.setY(block.getY());
		data.setZ(block.getZ());
		if (player!=null) {
			data.playerName = player.getName();
			data.playerUUID = player.getUniqueId().toString();
		}
		data.sign = id.id();
		return data;
	}

}
