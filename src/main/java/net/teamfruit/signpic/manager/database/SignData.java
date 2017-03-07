package net.teamfruit.signpic.manager.database;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.kamesuta.mc.signpic.entry.EntryId;

import net.teamfruit.signpic.manager.SignPictureManager;

@Entity
@Table(name = "sign")
public class SignData extends DomainData {

	@NotEmpty
	private @Nullable String worldName;

	@NotNull
	private int x;

	@NotNull
	private int y;

	@NotNull
	private int z;

	@NotEmpty
	private @Nullable String sign;

	@NotNull
	private @Nullable String playerName;

	@NotNull
	private @Nullable String playerUUID;

	public Location getLocation() {
		final World world = Bukkit.getServer().getWorld(this.worldName);
		return new Location(world, this.x, this.y, this.z);
	}

	public String getWorldName() {
		if (this.worldName!=null)
			return this.worldName;
		return this.worldName = "";
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public void setWorldName(final String worldName) {
		this.worldName = worldName;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public void setZ(final int z) {
		this.z = z;
	}

	public @Nullable Player getPlayer() {
		if (this.playerName!=null)
			return Bukkit.getServer().getPlayer(this.playerName);
		return null;
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

	public void setSign(final String sign) {
		this.sign = sign;
	}

	public void setPlayerName(final String playerName) {
		this.playerName = playerName;
	}

	public void setPlayerUUID(final String playerUUID) {
		this.playerUUID = playerUUID;
	}

	@Override
	public String toString() {
		return SignPictureManager.gson.toJson(this);
	}

	public static SignData fromEvent(final Block block, final @Nullable Player player, final EntryId id) {
		final SignData data = new SignData();
		data.setWorldName(block.getWorld().getName());
		data.setX(block.getX());
		data.setY(block.getY());
		data.setZ(block.getZ());
		if (player!=null) {
			data.setPlayerName(player.getName());
			data.setPlayerUUID(player.getUniqueId().toString());
		}
		data.setSign(id.id());
		return data;
	}

}
