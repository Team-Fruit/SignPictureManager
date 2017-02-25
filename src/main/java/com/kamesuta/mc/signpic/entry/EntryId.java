package com.kamesuta.mc.signpic.entry;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.kamesuta.mc.signpic.entry.content.ContentId;

import net.teamfruit.signpic.manager.meta.SignMeta;

public class EntryId {
	public static final EntryId blank = new EntryId("");

	private final String id;

	protected EntryId(final String id) {
		this.id = id;
	}

	public String id() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+id().hashCode();
		return result;
	}

	@Override
	public boolean equals(final @Nullable Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof EntryId))
			return false;
		final EntryId other = (EntryId) obj;
		if (!id().equals(other.id()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("EntryId [id=%s]", id());
	}

	public static EntryId from(final @Nullable String string) {
		if (string!=null&&!StringUtils.isEmpty(string))
			return new EntryId(string);
		return blank;
	}

	public static EntryId fromStrings(final String[] strings) {
		return from(StringUtils.join(strings));
	}

	private boolean hasContentId() {
		return !(StringUtils.isEmpty(id())||StringUtils.containsOnly(id(), "!")||StringUtils.containsOnly(id(), "$"));
	}

	private boolean hasMeta() {
		return StringUtils.endsWith(id(), "]")&&StringUtils.contains(id(), "[")||
				hasPrefix()&&StringUtils.endsWith(id(), "}")&&StringUtils.contains(id(), "{");
	}

	public boolean isOutdated() {
		return StringUtils.endsWith(id(), "]")&&StringUtils.contains(id(), "[");
	}

	private boolean hasPrefix() {
		final int i = StringUtils.indexOf(id(), "#");
		return 0<=i&&i<2;
	}

	public boolean isValid() {
		return hasContentId()&&hasMeta();
	}

	public @Nullable String getPrePrefix() {
		if (StringUtils.indexOf(id(), "#")==1)
			return StringUtils.substring(id(), 0, 0);
		else
			return null;
	}

	public @Nullable ContentId getContentId() {
		if (hasContentId()) {
			String id;
			if (StringUtils.contains(id(), "["))
				id = StringUtils.substring(id(), 0, StringUtils.lastIndexOf(id(), "["));
			else if (hasPrefix()&&StringUtils.contains(id(), "{"))
				id = StringUtils.substring(id(), StringUtils.indexOf(id(), "#")+1, StringUtils.lastIndexOf(id(), "{"));
			else
				id = id();
			return new ContentId(id);
		}
		return null;
	}

	public @Nullable String getMetaSource() {
		if (hasMeta())
			if (StringUtils.endsWith(id(), "}"))
				return StringUtils.substring(id(), StringUtils.lastIndexOf(id(), "{")+1, StringUtils.length(id())-1);
			else
				return StringUtils.substring(id(), StringUtils.lastIndexOf(id(), "[")+1, StringUtils.length(id())-1);
		return null;
	}

	public @Nullable SignMeta getMeta() {
		final String metasource = getMetaSource();
		if (metasource!=null)
			return new SignMeta(metasource);
		return null;
	}

	public boolean isPlaceable() {
		return StringUtils.length(id())<=15*4;
	}

	public boolean isNameable() {
		return StringUtils.length(id())<=40;
	}

	public void toStrings(final @Nullable String[] sign) {
		if (sign!=null) {
			final int length = StringUtils.length(id());
			for (int i = 0; i<4; i++)
				sign[i] = StringUtils.substring(id(), 15*i, Math.min(15*(i+1), length));
		}
	}

	public int getLastLine() {
		return StringUtils.length(id())/15;
	}

}
