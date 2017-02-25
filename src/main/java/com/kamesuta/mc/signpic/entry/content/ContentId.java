package com.kamesuta.mc.signpic.entry.content;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

public class ContentId {
	private final String id;

	public ContentId(String uri) {
		if (StringUtils.contains(uri, "http://"))
			uri = ""+StringUtils.substring(uri, 7, StringUtils.length(uri));
		else if (StringUtils.contains(uri, "https://"))
			uri = "$"+StringUtils.substring(uri, 8, StringUtils.length(uri));
		this.id = uri;
	}

	public String getID() {
		return this.id;
	}

	public String getURI() {
		if (!StringUtils.startsWith(this.id, "!"))
			if (StringUtils.startsWith(this.id, "$"))
				return "https://"+StringUtils.substring(this.id, 1);
			else if (!StringUtils.startsWith(this.id, "http://")&&!StringUtils.startsWith(this.id, "https://"))
				return "http://"+this.id;
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+this.id.hashCode();
		return result;
	}

	@Override
	public boolean equals(final @Nullable Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof ContentId))
			return false;
		final ContentId other = (ContentId) obj;
		if (!this.id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("ContentId [id=%s]", this.id);
	}

	public boolean isResource() {
		return this.id.startsWith("!");
	}
}
