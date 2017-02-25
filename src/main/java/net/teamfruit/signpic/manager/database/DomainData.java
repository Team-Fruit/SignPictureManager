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
	private @Nullable Integer id;

	@CreatedTimestamp
	private @Nullable Date createDate;

	@Version
	private @Nullable Date updateDate;

	public Integer getId() {
		if (this.id!=null)
			return this.id;
		return this.id = 0;
	}

	public Date getCreateDate() {
		if (this.createDate!=null)
			return this.createDate;
		return this.createDate = new Date();
	}

	public Date getUpdateDate() {
		if (this.updateDate!=null)
			return this.updateDate;
		return this.updateDate = new Date();
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	public void setUpdateDate(final Date updateDate) {
		this.updateDate = updateDate;
	}
}
