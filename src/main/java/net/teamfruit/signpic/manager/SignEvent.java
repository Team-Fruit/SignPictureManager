package net.teamfruit.signpic.manager;

import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.material.Sign;

import com.kamesuta.mc.signpic.entry.EntryId;
import com.kamesuta.mc.signpic.entry.content.ContentId;

import net.teamfruit.signpic.manager.database.SignDataBase;
import net.teamfruit.signpic.manager.meta.SignMeta;

public class SignEvent implements Listener {
	private final Logger logger;
	public @Nullable final SignDataBase signdata;
	public final FileConfiguration config;

	public SignEvent(final SignPictureManager plugin) {
		this.logger = plugin.getLogger();
		this.signdata = plugin.signdata;
		this.config = plugin.getConfig();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(final BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		final Block b = event.getBlock();
		if (b.getType()==Material.WALL_SIGN||b.getType()==Material.SIGN_POST)
			signDisable(b);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityExplode(final EntityExplodeEvent event) {
		if (event.isCancelled())
			return;
		for (final Block b : event.blockList())
			if (b.getType()==Material.WALL_SIGN||b.getType()==Material.SIGN_POST)
				signDisable(b);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		final Block b = event.getBlock();
		if (b.getType()==Material.WALL_SIGN||b.getType()==Material.SIGN_POST) {
			final Sign s = (Sign) b.getState().getData();
			final Block attachedBlock = b.getRelative(s.getAttachedFace());
			if (!attachedBlock.getType().isSolid())
				signDisable(b);
		}
	}

	@EventHandler
	public void onNormalSignChange(final SignChangeEvent sign) {
		if (sign.isCancelled())
			return;
		final EntryId id = EntryId.fromStrings(sign.getLines());
		if (id.isValid())
			sign.setCancelled(!checkPerm(sign.getBlock().getWorld(), sign.getPlayer(), id));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitorSignChange(final SignChangeEvent sign) {
		if (sign.isCancelled())
			return;
		signEnable(sign.getBlock(), sign.getPlayer(), EntryId.fromStrings(sign.getLines()));
	}

	public void signEnable(final Block b, final Player p, final EntryId d) {
		try {
			this.logger.info("Placed!");
			if (this.signdata!=null)
				this.signdata.setSign(b, p, d);
		} catch (final Exception e) {
			this.logger.info(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void signDisable(final Block b) {
		try {
			this.logger.info("Removed!");
			if (this.signdata!=null)
				this.signdata.removeSign(b);
		} catch (final Exception e) {
			this.logger.info(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public boolean checkPerm(final World world, final Player player, final EntryId id) {
		if (!player.hasPermission("signpic.place"))
			return false;
		if (player.hasPermission("signpic.ignoresettings"))
			return true;
		for (final String str : this.config.getStringList("dimBlacklist"))
			if (world.getName().equalsIgnoreCase(str))
				return false;
		final ContentId content = id.getContentId();
		if (content!=null)
			if (StringUtils.endsWithIgnoreCase(content.getURI(), ".gif")&&!player.hasPermission("signpic.place.gif"))
				return false;
		final SignMeta meta = id.getMeta();
		if (meta!=null) {
			if (meta.animations.isInclude()&&!player.hasPermission("signpic.place.animation"))
				return false;
			if (meta.rotations.isInclude()&&!player.hasPermission("signpic.place.rotation"))
				return false;
			if (meta.offsets.isInclude()) {
				if (!player.hasPermission("signpic.place.offset"))
					return false;
				if (meta.offsets.data().x.get()>this.config.getInt("limits.offset"))
					return false;
				if (meta.offsets.data().y.get()>this.config.getInt("limits.offset"))
					return false;
				if (meta.offsets.data().z.get()>this.config.getInt("limits.offset"))
					return false;
			}
			if (meta.sizes.data().height>this.config.getInt("limits.size"))
				return false;
			if (meta.sizes.data().width>this.config.getInt("limits.size"))
				return false;
		}
		return true;
	}
}