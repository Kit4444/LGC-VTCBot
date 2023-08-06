package eu.lotusgc.bot_vtc.main;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgc.bot_vtc.event.generic.ReadyClass;
import eu.lotusgc.bot_vtc.misc.ConfigFileHandler;
import eu.lotusgc.bot_vtc.misc.MySQL;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	
	public static final Logger logger = Logger.getLogger("LGC Public Bot");

	public static void main(String[] args) {
		try {
			ConfigFileHandler.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		enableShutdownHook();
		configLogger();
		try {
			YamlFile cfg = YamlFile.loadConfiguration(ConfigFileHandler.mainConfig);
			connectSQL(cfg);
			startBot(cfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void startBot(YamlFile cfg) {
		JDABuilder builder = JDABuilder.createDefault(cfg.getString("Bot.token"));
		builder.enableIntents(GatewayIntent.GUILD_MODERATION, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES);
		builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.FORUM_TAGS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.VOICE_STATE);
		builder.addEventListeners(new ReadyClass());
		builder.build();
	}
	
	private static void connectSQL(YamlFile cfg) {
		if(cfg.getBoolean("MySQL.UseSQL")) {
			logger.info("Bot is using SQL!");
			try {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}else {
			logger.info("Bot is not using SQL!");
		}
	}
	
	private static void configLogger() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String date = sdf.format(new Date());
		try {
			FileHandler fileHandler = new FileHandler(ConfigFileHandler.configFolderName + "/logs/log-" + date + ".log.txt");
			fileHandler.setFormatter(new SimpleFormatter());
			
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.ALL);
			
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
			
			logger.setLevel(Level.ALL);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void closeLogger() {
		for(Handler handler : logger.getHandlers()) {
			handler.close();
		}
	}
	
	private static void enableShutdownHook() {
		Thread printingHook = new Thread(() -> {
			MySQL.disconnect();
			logger.info("Bot is in shutdownprogress, byebye...");
			closeLogger();
		});
		Runtime.getRuntime().addShutdownHook(printingHook);
	}
}