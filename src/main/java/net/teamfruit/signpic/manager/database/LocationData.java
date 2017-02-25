package net.teamfruit.signpic.manager.database;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@MappedSuperclass
public class LocationData extends DomainData {

	@NotEmpty
	private @Nullable String worldName;

	@NotNull
	private @Nullable Integer x;

	@NotNull
	private @Nullable Integer y;

	@NotNull
	private @Nullable Integer z;

	public @Nullable Location getLocation() {
		final World world = Bukkit.getServer().getWorld(this.worldName);
		if (this.x!=null&&this.y!=null&&this.z!=null)
			return new Location(world, this.x, this.y, this.z);
		return null;
	}

	public String getWorldName() {
		if (this.worldName!=null)
			return this.worldName;
		return this.worldName = "";
	}

	public Integer getX() {
		if (this.x!=null)
			return this.x;
		return this.x = 0;
	}

	public Integer getY() {
		if (this.y!=null)
			return this.y;
		return this.y = 0;
	}

	public Integer getZ() {
		if (this.z!=null)
			return this.z;
		return this.z = 0;
	}

	public void setWorldName(final String worldName) {
		this.worldName = worldName;
	}

	public void setX(final Integer x) {
		this.x = x;
	}

	public void setY(final Integer y) {
		this.y = y;
	}

	public void setZ(final Integer z) {
		this.z = z;
	}
}
