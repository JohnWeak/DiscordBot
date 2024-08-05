import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class Clash
{
	private final Object obj = this;
	
	private static final JSONParser jsonParser = new JSONParser();
	private static final String bearer = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjYwOGY2MGU5LTQwZjUtNDQ2YS1iMGIyLTY3ZDE0ODk1ZDgyZiIsImlhdCI6MTY3NTg3MjQwMCwic3ViIjoiZGV2ZWxvcGVyLzRhNmIzZDczLTMyZjktNDRkMS0xMGMzLWMzOTcxMDA2YzI4YiIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk0LjcyLjE0My4xNzYiXSwidHlwZSI6ImNsaWVudCJ9XX0.Fl8VXJovFzV4PY1Qcfw3EdqhBCjNEKnYWheNFNL3zfr0ryoaB-skrkow1bYxpUx7hVVXyqLAeoqaO-_3pgLeUw";
	
	public static final String hashtag = "%23";
	public static final String clanTag = "PLQP8UJ8";
	public static final String tagCompleto = hashtag + clanTag;
	
	private final static String warLeague = "https://api.clashofclans.com/v1/clans/"+ tagCompleto +"/currentwar/leaguegroup";
	
	
	/**Controlla se il clan è in war e mostra l'andamento*/
	public void clashWar()
	{
		final var currentWar = "https://api.clashofclans.com/v1/clans/"+ tagCompleto + "/currentwar";
		final EmbedBuilder embedToSend = new EmbedBuilder().setColor(Color.RED);
		
		try
		{
			final URL currentWarURL = new URL(currentWar);
			final String response = getResponse(currentWarURL);
			final JSONParser jsonParser = new JSONParser();
			final Object obj = jsonParser.parse(response);
			final JSONObject jsonObject = (JSONObject) obj;
			final String state = (String) jsonObject.get("state");
			final JSONObject clan = (JSONObject) jsonObject.get("clan");
			final JSONObject clanBadgeUrls = (JSONObject) clan.get("badgeUrls");
			final String clanBadgeS = (String) clanBadgeUrls.get("small");
			final String clanBadgeM = (String) clanBadgeUrls.get("medium");
			final String clanBadgeL = (String) clanBadgeUrls.get("large");

			if (state.equalsIgnoreCase("notinwar"))
			{
				embedToSend
					.addField("Not in war", "Non siamo in guerra al momento, smh.", false)
					.setTimestamp(Instant.now())
					.setAuthor("War", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", clanBadgeM)
				;

				new Commands().sendEmbedToChannel(embedToSend.build(), false);
				return;
			}

			final String[] percentage = new String[2];
			final String[] attacks = new String[2];
			final String[] stars = new String[2];
			
			final String name = (String) clan.get("name");
			percentage[0] = String.format("%.2f", (double) clan.get("destructionPercentage"));
			attacks[0] = (String) clan.get("attacks");
			stars[0] = (String) clan.get("stars");

			final JSONObject opponent = (JSONObject) jsonObject.get("opponent");
			final String oppName = (String) opponent.get("name");
			
			final JSONObject oppBagdeUrls = (JSONObject) opponent.get("badgeUrls");
			final String oppBadgeS = (String) oppBagdeUrls.get("small");
			final String oppBadgeM = (String) oppBagdeUrls.get("medium");
			final String oppBadgeL = (String) oppBagdeUrls.get("large");
			
			percentage[1] = String.format("%.2f", (double) opponent.get("destructionPercentage"));
			attacks[1] = (String) opponent.get("attacks");
			stars[1] = (String) opponent.get("stars");
			
			final String[] atk = grassetto(attacks);
			final String[] str = grassetto(stars);
			final String[] destr = grassetto(percentage);
			
			final String st = str[0] + " vs " + str[1];
			final String attacchi = atk[0] + " vs " +atk[1];
			final String distr = destr[0] + " vs " + destr[1];
			
			embedToSend
				.setTitle("**" + name + " contro " + oppName +"**")
				.setThumbnail(oppBadgeL)
				.addField("Stelle",st+"\t", true)
				.addField("Attacchi", attacchi+"\t", true)
				.addField("Distruzione",distr+"\t",true)
			;

			new Commands().sendEmbedToChannel(embedToSend.build(), false);
			
		}
		catch (IOException | ParseException e)
		{
			new Error<Exception>().print(obj, e);
		}
	} // fine clashWar()
	
	/**Controlla se il clan è attualmente in guerra nella lega tra clan*/
	public boolean isClanInLeague()
	{
		try
		{
			final URL warLeagueURL = new URL(warLeague);
			final String response = getResponse(warLeagueURL);
			
			final JSONParser jsonParser = new JSONParser();
			final Object obj = jsonParser.parse(response);
			final JSONObject jsonObject = (JSONObject) obj;
			
			if (((String) jsonObject.get("state")).equalsIgnoreCase("inwar"))
				return true;
			
		}catch (IOException | ParseException e) { new Error<Exception>().print(obj, e); }
		
		return false;
	} // fine isClanInLeague()
	
	/**Ottiene e mostra le informazioni sulla war della lega tra clan*/
	public void clashWarLeague(boolean thread)
	{
		if (isClanInLeague())
		{
			final int c = new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
			final int dayOfWar = c-3-1;

			try
			{
				final URL warLeagueURL = new URL(warLeague);
				final String response = getResponse(warLeagueURL);
				
				final JSONParser jsonParser = new JSONParser();
				final Object obj = jsonParser.parse(response);
				final JSONObject jsonObject = (JSONObject) obj;
				
				final JSONArray warTagsArray = (JSONArray) jsonObject.get("rounds");
				
				final JSONObject warDays = (JSONObject) warTagsArray.get(dayOfWar);
				final JSONArray warTags = (JSONArray) warDays.get("warTags");
				
				final EmbedBuilder embed = search(warTags, dayOfWar);
				
				new Commands().sendEmbedToChannel(Objects.requireNonNullElseGet(embed, () -> new EmbedBuilder().addField("Oh noes", "Errore catastrofico (non è vero) in clashWarLeague()", false)).build(), thread);
			}catch (Exception e) { new Error<Exception>().print(obj, e); }
		}
		
	} // fine clashWarLeague()
	
	public static EmbedBuilder search(JSONArray tags, int dayOfWar)
	{
		dayOfWar++;
		EmbedBuilder embed = null;
		final String legaURL = "https://api.clashofclans.com/v1/clanwarleagues/wars/%23";
		
		for (int i = 0; i < 4; i++)
		{
			final String x = tags.get(i).toString().substring(1);
			
			try
			{
				final URL url = new URL(legaURL+x);
				final String response = getResponse(url);
				
				final Object obj = jsonParser.parse(response);
				final JSONObject jsonObject = (JSONObject) obj;
				final JSONObject clan = (JSONObject) jsonObject.get("clan");
				final JSONObject clanBadgeUrls = (JSONObject) clan.get("badgeUrls");
				final String clanBadgeM = (String) clanBadgeUrls.get("medium");
				final String name = (String) clan.get("name");
				final JSONObject opponent = (JSONObject) jsonObject.get("opponent");
				final String oppName = (String) opponent.get("name");
				final JSONObject opponentBadgeUrls = (JSONObject) opponent.get("badgeUrls");
				final String opponentBadgeM = (String) opponentBadgeUrls.get("medium");
				final String opponentBadgeL = (String) opponentBadgeUrls.get("large");
				boolean nameIsUs = true;
				
				if (name.equalsIgnoreCase("the legends") || oppName.equalsIgnoreCase("the legends"))
				{
					nameIsUs = name.equalsIgnoreCase("the legends");
					
					final String[] stars = new String[2];
					final String[] attacks = new String[2];
					final String[] percentage = new String[3];
					
					stars[0] = String.format("%d", (long) clan.get("stars"));
					attacks[0] = String.format("%d", (long) clan.get("attacks"));
					percentage[0] = String.valueOf((double) clan.get("destructionPercentage"));
					
					
					stars[1] = String.format("%d", (long) opponent.get("stars"));
					attacks[1] = String.format("%d", (long) opponent.get("attacks"));
					percentage[1] = String.valueOf((double) opponent.get("destructionPercentage"));
					
					final String[] str = grassetto(stars);
					final String[] atk = grassetto(attacks);
					final String[] destr = grassetto(percentage);
					
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
					
					final String nome = (nameIsUs ? name : oppName);
					final String nomeNemici = (nameIsUs ? oppName : name);
					final String st = (nameIsUs ? str[0]:str[1]) + " vs " + (nameIsUs?str[1]:str[0]);
					final String attacchi = (nameIsUs ? atk[0]:atk[1]) +" vs "+ (nameIsUs?atk[1]:atk[0]);
					final String distr = (nameIsUs?destr[0]:destr[1]) + " vs "+ (nameIsUs?destr[1]:destr[0]);
					
					embed = new EmbedBuilder()
						.setTitle("**" + nome + "** contro **" + nomeNemici +"**")
						.setColor(Color.RED)
						.setTimestamp(Instant.now())
						.setAuthor("Guerra " + dayOfWar + " di 7", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", ""+clanBadgeM)
						.setThumbnail(opponentBadgeL)
						.addField("Stelle",st+"\t", true)
						.addField("Attacchi", attacchi+"\t", true)
						.addField("Distruzione",distr+"\t",true)
					;
				}
				
			}catch (IOException | ParseException ignored){}
		}
		return embed;
	} // fine search()
	
	private static String getResponse(URL url) throws IOException
	{
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("authorization", bearer);
		
		final InputStream responseStream = connection.getInputStream();
		
		final BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
		String inputLine = "";
		
		final StringBuilder response = new StringBuilder();
		
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		in.close();
		
		return String.valueOf(response);
	} // fine getResponse()
	
	public static String[] grassetto(String[] numeri)
	{
		final double uno = Double.parseDouble(numeri[0]);
		final double due = Double.parseDouble(numeri[1]);
		final int unoIndex = numeri[0].indexOf(".");
		final int dueIndex = numeri[1].indexOf(".");
		
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
	
} // fine classe Clash
