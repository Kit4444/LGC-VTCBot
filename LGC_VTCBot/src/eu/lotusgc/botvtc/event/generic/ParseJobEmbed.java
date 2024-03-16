package eu.lotusgc.botvtc.event.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.lotusgc.botvtc.main.Main;
import eu.lotusgc.botvtc.misc.MySQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ParseJobEmbed extends ListenerAdapter{
	
	static long guildId = 1092011696287125574l;
	static long jobChannelId = 1094293601783124070l;
	static long jobDebugId = 1137839526325276802l;
	
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isFromType(ChannelType.TEXT)) {
			if(event.getAuthor().isBot()) {
				TextChannel channel = event.getChannel().asTextChannel();
				Guild guild = event.getGuild();
				if(guild.getIdLong() == guildId) {
					TextChannel debugChannel = guild.getTextChannelById(jobDebugId);
					if(channel.getIdLong() == jobChannelId) {
						if(event.getMessage().getEmbeds().size() != 0) {
							MessageEmbed me = event.getMessage().getEmbeds().get(0); //considering the first embed is the one with the jobId to get the rest from trucky api.
							String jobId = me.getTitle().split("-")[1];
							jobId = jobId.substring(2, jobId.length());
							debugChannel.sendMessage("Ausgabe soll Job ID sein, Ausgabe ist: " + jobId).queue();
						}
					}
				}
			}else {
				Main.logger.info("Message " + event.getJumpUrl() + " is not from a webhook, thus will be ignored.");
			}
		}
	}
	
	private JsonObject getJobData(String jobId) {
		try {
			URL url = new URL("https://e.truckyapp.com/api/v1/job/" + jobId);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = "";
			String jsonS = "";
			while((inputLine = in.readLine()) != null) {
				jsonS += inputLine;
			}
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
			return jsonObject;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean existJob(int jobId) {
		boolean exists = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT jobId FROM vtc_jobs WHERE jobId = ?");
			ps.setInt(1, jobId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				exists = true;
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}
	
	//https://e.truckyapp.com/api/v1/job/6986172
	private void addJobToDB(JsonObject obj) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO vtc_jobs jobId = ?, userId = ?, gameId = ?, gamemode = ?, vehicle_brand = ?, vehicle_model = ?, trailer_body = ?, trailer_chain = ?, drivenKm = ?, source_city = ?, source_company = ?, destination_city = ?, destination_company = ?, cargo_name = ?, cargo_units = ?, cargo_unit_mass = ?, money_revenue = ?, money_taxes = ?, usedFuel = ?, isLate = ?, driveTime = ?, statistics = ?, maxSpeed = ?, avgSpeed = ?");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}