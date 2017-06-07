package net.teamfruit.signpic.manager;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
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
	private final Log logger;
	public final SignDataBase signdata;
	public final FileConfiguration config;
	public final I18n i18n;

	public SignEvent(final SignPictureManager plugin) {
		this.logger = plugin.getLog();
		this.signdata = plugin.getSignData();
		this.config = plugin.getConfig();
		this.i18n = plugin.getI18n();
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onNormalSignChange(final SignChangeEvent sign) {
		if (sign.isCancelled())
			return;
		final EntryId id = EntryId.fromStrings(sign.getLines());
		if (id.isValid()) {
			final boolean permission = checkPerm(sign.getBlock().getWorld(), sign.getPlayer(), id);
			if (!permission) {
				sign.setCancelled(true);
				if (this.config.getBoolean("replaceSignText")) {
					final String text = this.i18n.format("sign.replaceText");
					for (int i = 0; i<=3; i++)
						sign.setLine(i, StringUtils.substring(text, i*15, (i+1)*15));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitorSignChange(final SignChangeEvent sign) {
		if (sign.isCancelled())
			return;
		signEnable(sign.getBlock(), sign.getPlayer(), EntryId.fromStrings(sign.getLines()));
	}

	public void signEnable(final Block b, final Player p, final EntryId d) {
		try {
			this.logger.fine("Placed!");
			this.signdata.setSign(b, p, d);
		} catch (final Exception e) {
			this.logger.info(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void signDisable(final Block b) {
		try {
			this.logger.fine("Removed!");
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
		final SignMeta meta = id.getMeta();
		if (meta!=null) {
			if (meta.animations.isInclude()&&!player.hasPermission("signpic.place.animation"))
				return false;
			if (meta.rotations.isInclude()&&!player.hasPermission("signpic.place.rotation"))
				return false;
			this.logger.info(meta.offsets.isInclude());
			if (meta.offsets.isInclude()) {
				if (!player.hasPermission("signpic.place.offset"))
					return false;
				final int offset = this.config.getInt("limits.offset");
				if (offset>0) {
					if (meta.offsets.data().x.get()>offset)
						return false;
					if (meta.offsets.data().y.get()>offset)
						return false;
					if (meta.offsets.data().z.get()>offset)
						return false;
				}
			}
			final int size = this.config.getInt("limits.size");
			if (size>0) {
				if (meta.sizes.data().height>size)
					return false;
				if (meta.sizes.data().width>size)
					return false;
			}
		}
		final ContentId content = id.getContentId();
		if (content!=null)
			if (!player.hasPermission("signpic.place.gif")) {
				if (StringUtils.endsWithIgnoreCase(content.getURI(), ".gif"))
					return false;
				if (isGif(content.getURI()))
					return false;
			}
		return true;
	}

	public boolean isGif(final String uri) {
		final HttpUriRequest req = new HttpGet(uri);
		try {
			final HttpResponse res = Downloader.downloader.client.execute(req, HttpClientContext.create());
			final String contentType = res.getFirstHeader("Content-Type").getValue();
			return "image/gif".equalsIgnoreCase(contentType);
		} catch (final IOException e) {
			this.logger.warning(e);
		} finally {
			req.abort();
		}
		return false;
	}
}