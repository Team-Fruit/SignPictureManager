package net.teamfruit.signpic.manager.database;

import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.avaje.ebean.annotation.CreatedTimestamp;

@MappedSuperclass
public class DomainData {

	@Id
	private int id;

	@CreatedTimestamp
	private @Nullable Date createDate;

	@Version
	private @Nullable Date updateDate;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public @Nullable Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(final Date date) {
		this.createDate = date;
	}

	public @Nullable Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(final Date date) {
		this.updateDate = date;
	}
}
