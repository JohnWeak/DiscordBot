import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class Clash
{
	private static final JSONParser jsonParser = new JSONParser();
	private static final String bearer = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjYwOGY2MGU5LTQwZjUtNDQ2YS1iMGIyLTY3ZDE0ODk1ZDgyZiIsImlhdCI6MTY3NTg3MjQwMCwic3ViIjoiZGV2ZWxvcGVyLzRhNmIzZDczLTMyZjktNDRkMS0xMGMzLWMzOTcxMDA2YzI4YiIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk0LjcyLjE0My4xNzYiXSwidHlwZSI6ImNsaWVudCJ9XX0.Fl8VXJovFzV4PY1Qcfw3EdqhBCjNEKnYWheNFNL3zfr0ryoaB-skrkow1bYxpUx7hVVXyqLAeoqaO-_3pgLeUw";
	
	public static final String hashtag = "%23";
	public static final String clanTag = "PLQP8UJ8";
	public static final String tagCompleto = hashtag + clanTag;
	
	private final static String warLeague = "https://api.clashofclans.com/v1/clans/"+ tagCompleto +"/currentwar/leaguegroup";
	
	// TODO: recuperare la lista dei membri del clan ogni volta che si invoca il costruttore
	private static final ArrayList<String> listaMembri = new ArrayList<>();
	
	
	/**Controlla se il clan è in war e mostra l'andamento*/
	public void clashWar()
	{
		final var currentWar = "https://api.clashofclans.com/v1/clans/"+ tagCompleto + "/currentwar";
		var embedToSend = new EmbedBuilder().setColor(Color.RED);
		
		try
		{
			final var currentWarURL = new URL(currentWar);
			var response = getResponse(currentWarURL);
			var jsonParser = new JSONParser();
			Object obj = jsonParser.parse(response);
			var jsonObject = (JSONObject) obj;
			var state = (String) jsonObject.get("state");
			var clan = (JSONObject) jsonObject.get("clan");
			var clanBadgeUrls = (JSONObject) clan.get("badgeUrls");
			var clanBadgeS = (String) clanBadgeUrls.get("small");
			var clanBadgeM = (String) clanBadgeUrls.get("medium");
			var clanBadgeL = (String) clanBadgeUrls.get("large");

			if (state.equalsIgnoreCase("notinwar"))
			{
				embedToSend
					.addField("Not in war", "Non siamo in guerra al momento, smh.", false)
					.setTimestamp(Instant.now())
					.setAuthor("War", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", ""+clanBadgeM)
				;

				new Commands().sendEmbedToChannel(embedToSend.build(), false);
				return;
			}

			String[] percentage = new String[2];
			String[] attacks = new String[2];
			String[] stars = new String[2];
			
			var name = (String) clan.get("name");
			percentage[0] = String.format("%.2f", (double) clan.get("destructionPercentage"));
			attacks[0] = (String) clan.get("attacks");
			stars[0] = (String) clan.get("stars");

			var opponent = (JSONObject) jsonObject.get("opponent");
			var oppName = (String) opponent.get("name");
			
			var oppBagdeUrls = (JSONObject) opponent.get("badgeUrls");
			var oppBadgeS = (String) oppBagdeUrls.get("small");
			var oppBadgeM = (String) oppBagdeUrls.get("medium");
			var oppBadgeL = (String) oppBagdeUrls.get("large");
			
			percentage[1] = String.format("%.2f", (double) opponent.get("destructionPercentage"));
			attacks[1] = (String) opponent.get("attacks");
			stars[1] = (String) opponent.get("stars");
			
			var atk = grassetto(attacks);
			var str = grassetto(stars);
			var destr = grassetto(percentage);
			
			var st = str[0] + " vs " + str[1];
			var attacchi = atk[0] + " vs " +atk[1];
			var distr = destr[0] + " vs " + destr[1];
			
			embedToSend
				.setTitle("**" + name + " contro " + oppName +"**")
				.setThumbnail(oppBadgeL)
				.addField("Stelle",""+st+"\t", true)
				.addField("Attacchi", ""+attacchi+"\t", true)
				.addField("Distruzione",""+distr+"\t",true)
			;

			new Commands().sendEmbedToChannel(embedToSend.build(), false);
			
		}
		catch (IOException | ParseException e)
		{
			Commands.canaleBot.sendMessage("`"+this.getClass()+"`\n"+e).queue();
		}
	} // fine clashWar()
	
	/**Controlla se il clan è attualmente in guerra nella lega tra clan*/
	public boolean isClanInLeague()
	{
		try
		{
			final var warLeagueURL = new URL(warLeague);
			var response = getResponse(warLeagueURL);
			
			var jsonParser = new JSONParser();
			Object obj = jsonParser.parse(response);
			var jsonObject = (JSONObject) obj;
			
			if (((String) jsonObject.get("state")).equalsIgnoreCase("inwar"))
				return true;
			
		}catch (IOException | ParseException e) { e.printStackTrace(); }
		
		return false;
	} // fine isClanInLeague()
	
	/**Ottiene e mostra le informazioni sulla war della lega tra clan*/
	public void clashWarLeague(boolean thread)
	{
		if (isClanInLeague())
		{
			var c = new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
			System.out.println("Giorno del mese: "+c);
			var dayOfWar = c-3-1;

			try
			{
				final var warLeagueURL = new URL(warLeague);
				var response = getResponse(warLeagueURL);
				
				var jsonParser = new JSONParser();
				Object obj = jsonParser.parse(response);
				var jsonObject = (JSONObject) obj;
				
				var warTagsArray = (JSONArray) jsonObject.get("rounds");
				
				var warDays = (JSONObject) warTagsArray.get(dayOfWar);
				var warTags = (JSONArray) warDays.get("warTags");
				
				var embed = search(warTags, dayOfWar);

				if (embed == null)
					new Commands().sendEmbedToChannel(new EmbedBuilder().addField("Oh noes","Errore catastrofico (non è vero) in clashWarLeague()", false).build(), thread);
				else
					new Commands().sendEmbedToChannel(embed.build(), thread);
			}catch (IOException | ParseException e) {e.printStackTrace();}
		}
		
	} // fine clashWarLeague()
	
	public static EmbedBuilder search(JSONArray tags, int dayOfWar)
	{
		dayOfWar++;
		EmbedBuilder embed = null;
		final var legaURL = "https://api.clashofclans.com/v1/clanwarleagues/wars/%23";
		
		for (int i = 0; i < 4; i++)
		{
			var x = (String) tags.get(i);
			x = x.substring(1);
			
			try
			{
				var url = new URL(legaURL+x);
				var response = getResponse(url);
				
				Object obj = jsonParser.parse(response);
				var jsonObject = (JSONObject) obj;
				var clan = (JSONObject) jsonObject.get("clan");
				var clanBadgeUrls = (JSONObject) clan.get("badgeUrls");
				var clanBadgeM = (String) clanBadgeUrls.get("medium");
				var name = (String) clan.get("name");
				var opponent = (JSONObject) jsonObject.get("opponent");
				var oppName = (String) opponent.get("name");
				var opponentBadgeUrls = (JSONObject) opponent.get("badgeUrls");
				var opponentBadgeM = (String) opponentBadgeUrls.get("medium");
				var opponentBadgeL = (String) opponentBadgeUrls.get("large");
				var nameIsUs = true;
				
				if (name.equalsIgnoreCase("the legends") || oppName.equalsIgnoreCase("the legends"))
				{
					nameIsUs = name.equalsIgnoreCase("the legends");
					
					var stars = new String[2];
					var attacks = new String[2];
					var percentage = new String[3];
					
					stars[0] = String.format("%d", (long) clan.get("stars"));
					attacks[0] = String.format("%d", (long) clan.get("attacks"));
					percentage[0] = String.valueOf((double) clan.get("destructionPercentage"));
					
					
					stars[1] = String.format("%d", (long) opponent.get("stars"));
					attacks[1] = String.format("%d", (long) opponent.get("attacks"));
					percentage[1] = String.valueOf((double) opponent.get("destructionPercentage"));
					
					var str = grassetto(stars);
					var atk = grassetto(attacks);
					var destr = grassetto(percentage);
					
					if (destr[2].equals("0"))
					{
						destr[0] = destr[0] + " **%**";
						destr[1] = destr[1] + "%";
					}
					else
					{
						destr[0] = destr[0] + "%";
						destr[1] = destr[1] + " **%**";
					}
					
					var nome = (nameIsUs ? name : oppName);
					var nomeNemici = (nameIsUs ? oppName : name);
					var st = (nameIsUs ? str[0]:str[1]) + " vs " + (nameIsUs?str[1]:str[0]);
					var attacchi = (nameIsUs ? atk[0]:atk[1]) +" vs "+ (nameIsUs?atk[1]:atk[0]);
					var distr = (nameIsUs?destr[0]:destr[1]) + " vs "+ (nameIsUs?destr[1]:destr[0]);
					
					embed = new EmbedBuilder()
						.setTitle("**" + nome + "** contro **" + nomeNemici +"**")
						.setColor(Color.RED)
						.setTimestamp(Instant.now())
						.setAuthor("Guerra " + dayOfWar + " di 7", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", ""+clanBadgeM)
						.setThumbnail(opponentBadgeL)
						.addField("Stelle",""+st+"\t", true)
						.addField("Attacchi", ""+attacchi+"\t", true)
						.addField("Distruzione",""+distr+"\t",true)
					;
					
				}
				
				
			}catch (IOException | ParseException ignored){}
		}
		return embed;
	} // fine search()
	
	private static String getResponse(URL url) throws IOException
	{
		var connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("authorization", bearer);
		
		var responseStream = connection.getInputStream();
		
		var in = new BufferedReader(new InputStreamReader(responseStream));
		var inputLine = "";
		
		var response = new StringBuilder();
		
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		in.close();
		
		return String.valueOf(response);
	} // fine getResponse()
	
	
	
	public static String[] grassetto(String[] numeri)
	{
		var uno = Double.parseDouble(numeri[0]);
		var due = Double.parseDouble(numeri[1]);
		var unoIndex = numeri[0].indexOf(".");
		var dueIndex = numeri[1].indexOf(".");
		
		if (uno % 1 == 0 && unoIndex > 0)
			numeri[0] = numeri[0].substring(0, numeri[0].indexOf("."));
		else if (uno % 1 != 0 && unoIndex > 0)
			numeri[0] = String.format("%.2f", uno);
		
		if (due % 1 == 0 && dueIndex > 0)
			numeri[1] = numeri[1].substring(0, numeri[1].indexOf("."));
		else if (due % 1 != 0 && dueIndex > 0)
			numeri[1] = String.format("%.2f", due);
		
		
		if (uno >= due)
			numeri[0] = "**" + numeri[0] + "**";
		else
			numeri[1] = "**" + numeri[1] + "**";
		
		if (numeri.length == 3) // se è array di distruzione
			numeri[2] = (uno >= due ? "0" : "1");

		System.out.println("\tNUMERI: "+Arrays.toString(numeri));

		return numeri;
	}
	
	
	public String getBearer()
	{
		return bearer;
	}
	
	
} // fine classe Bot.Clash
