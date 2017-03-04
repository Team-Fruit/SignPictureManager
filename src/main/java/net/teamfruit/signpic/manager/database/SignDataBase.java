package net.teamfruit.signpic.manager.database;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.avaje.ebean.EbeanServer;
import com.kamesuta.mc.signpic.entry.EntryId;

import net.teamfruit.signpic.manager.SignPictureManager;

public class SignDataBase {
	private final SignPictureManager plugin;
	private final EbeanServer db;

	public SignDataBase(final SignPictureManager plugin) {
		this.plugin = plugin;
		this.db = plugin.getDatabase();
	}

	public void setSign(final Block block, final @Nullable Player player, final EntryId id) {
		if (id.isValid())
			this.db.save(SignData.fromEvent(block, player, id));
	}

	public void removeSign(final Block block) {
		final SignData data = this.db.find(SignData.class).where()
				.ieq("worldName", block.getWorld().getName())
				.ieq("x", String.valueOf(block.getX()))
				.ieq("y", String.valueOf(block.getY()))
				.ieq("z", String.valueOf(block.getZ())).findUnique();
		if (data!=null)
			this.db.delete(data);
	}

	public SignData getSign(final int id) {
		return this.db.find(SignData.class).where().eq("id", id).findUnique();
	}

	public List<SignData> getSigns() {
		return this.db.find(SignData.class).findList();
	}

	public String getSignsJson() {
		return SignPictureManager.gson.toJson(getSigns());
	}
}
