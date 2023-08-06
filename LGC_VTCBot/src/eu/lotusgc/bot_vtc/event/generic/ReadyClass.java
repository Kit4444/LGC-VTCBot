package eu.lotusgc.bot_vtc.event.generic;

import eu.lotusgc.bot_vtc.main.Main;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent e) {
		Main.logger.info("Bot is logged in and ready to use.");
	}

}
