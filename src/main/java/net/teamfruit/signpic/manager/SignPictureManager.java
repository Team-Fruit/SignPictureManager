package net.teamfruit.signpic.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.teamfruit.signpic.manager.command.OpenCommand;
import net.teamfruit.signpic.manager.command.ScanCommand;
import net.teamfruit.signpic.manager.command.SignPicCommand;
import net.teamfruit.signpic.manager.database.SignData;
import net.teamfruit.signpic.manager.database.SignDataBase;
import net.teamfruit.signpic.manager.packet.SignMessageListener;
import net.teamfruit.signpic.manager.scan.ScanManager;

public class SignPictureManager extends JavaPlugin {
	public static Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

	public @Nullable SignDataBase signdata;
	public Map<String, List<SignData>> tokendata = Maps.newHashMap();
	public @Nullable ScanManager scannerManager;
	public @Nullable I18n i18n;

	@Override
	public void onLoad() {
		getLogger().info("Load 1");
		getDataFolder().mkdirs();
	}

	@Override
	public void onEnable() {
		getLogger().info("Enable");
		try {
			//init config.yml
			initConfing();

			//init i18n
			final String langName = getConfig().getString("lang");
			final String langFileName = StringUtils.endsWithIgnoreCase(langName, ".yml") ? langName : langName+".yml";
			final File langFile = new File(getDataFolder(), "lang/"+langFileName);
			if (!langFile.exists())
				saveResource("lang/"+langFileName, false);
			this.i18n = new I18n(YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(langFile), Charsets.UTF_8)));

			//init DB
			initDatabase();
			this.signdata = new SignDataBase(this);

			//init plugin event listener
			getServer().getPluginManager().registerEvents(new SignEvent(this), this);

			//init packet handler
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "signpic.list");
			Bukkit.getMessenger().registerIncomingPluginChannel(this, "signpic.list", new SignMessageListener(this));

			//init commands
			final SignPicCommand rootCommand = new SignPicCommand(this, "signpicturemanager");
			getCommand("signpicturemanager").setExecutor(rootCommand);
			rootCommand.registerSubCommand(new OpenCommand(this));
			rootCommand.registerSubCommand(new ScanCommand(this));

			//init scan manager
			this.scannerManager = new ScanManager(this);
			this.scannerManager.onEnable();
		} catch (final Exception e) {
			getLogger().info(ExceptionUtils.getFullStackTrace(e));
			getPluginLoader().disablePlugin(this);
		}
	}

	private void initDatabase() {
		try {
			getDatabase().find(SignData.class).findRowCount();
		} catch (final PersistenceException ex) {
			getLogger().info("Installing database for "+getDescription().getName()+" due to first time usage");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		final List<Class<?>> list = Lists.newArrayList();
		list.add(SignData.class);
		return list;
	}

	@Override
	public void onDisable() {
		getLogger().info("Disable");
		initConfing();
		if (this.scannerManager!=null)
			this.scannerManager.onDisable();
	}

	public void initConfing() {
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		reloadConfig();
		saveConfig();
	}
}
