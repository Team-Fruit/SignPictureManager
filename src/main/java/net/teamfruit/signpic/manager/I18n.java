package net.teamfruit.signpic.manager;

import org.bukkit.configuration.file.FileConfiguration;

public class I18n {

	private final FileConfiguration lang;

	public I18n(final FileConfiguration langConfig) {
		this.lang = langConfig;
	}

	public String format(final String key) {
		final String str = this.lang.getString(key);
		if (str!=null)
			return str;
		return key;
	}

	public String format(final String key, final Object... args) {
		return String.format(format(key), args);
	}

	public String[] listFormat(final String key) {
		return this.lang.getStringList(key).toArray(new String[0]);
	}

	public int getVersion() {
		return this.lang.getInt("config-version");
	}
}
