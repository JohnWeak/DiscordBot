import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**Classe che contiene metodi utili e non intasare la classe Commands.*/
public abstract class Utilities
{
	/** Rende maiuscola la prima lettera della stringa, le altre in minuscolo
	 * @param s la stringa in input
	 * @return la stringa con la prima lettera in maiuscolo.*/
	public static String capitalize(String s)
	{
		return String.format("%s%s", s.substring(0, 1).toUpperCase(), s.substring(1).toLowerCase());
	}
	
	/**Recupera gli ultimi messaggi dal canale testuale desiderato.
	 * @param channel il canale testuale di cui bisogna prendere i messaggi.
	 * @param debug booleano che manda come messaggio privato il risultato a Gion.
	 * @param amount la quantità di messaggi da recuperare.
	 *
	 * @return la lista dei messaggi nella cronologia. <code>list[0]</code> = ultimo messaggio*/
	public static List<Message> channelHistory(GuildMessageChannel channel, boolean debug, int amount)
	{
		final List<Message> history = channel.getHistory().retrievePast(amount).complete();
		final StringBuilder historyMessages = new StringBuilder();
		
		if (debug)
		{
			final PrivateMessage pm = new PrivateMessage(Utente.getGion());
			final int size = history.size();
			
			for (int i = 0; i < size; i++)
			{
				final Message message = history.get(i);
				final User auth = message.getAuthor();
				final String name = auth.getName();
				final String disc = auth.getDiscriminator(); // nota: deprecato?
				final String m = message.getContentStripped();
				final String msg = String.format("Messaggio numero %d:\t%s --- %s (%s): %s\n", i, auth, name, disc, m);
				
				historyMessages.append(msg);
			}
			pm.send(historyMessages.toString());
		}
		return history;
		
	} // fine metodo channelHistory()
	
	/** Trasforma il testo da normale a parodia CaMeL cAsE.
	 * @param msg il testo originale.
	 * @return la stringa originale adesso trasformata in maiuscole e minuscole alternate.
	 * */
	public static String camelCase(String msg)
	{
		final char[] chars = msg.toCharArray();
		final int len = chars.length;
		char c;
		
		for (int i = 0; i < len; i++)
		{
			c = chars[i];
			chars[i] = (i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
		}
		
		return new String(chars);
	} // fine camelCase()
	
	/**Determina l'ora del giorno e restituisce la stringa del saluto corrispondente*/
	public static String getSaluto()
	{
		final GregorianCalendar c = getLocalizedCalendar();
		String saluto = "";
		final int hour = c.get(Calendar.HOUR_OF_DAY);
		final int month = c.get(Calendar.MONTH);
		final short tramonto;
		
		switch (month) // se è estate, il tramonto avviene più tardi
		{
			case 4, 5, 6, 7 -> tramonto = 20;
			default -> tramonto = 17;
		}
		
		if (hour > 0 && hour < 7)
			saluto = "Buona mattina";
		else if (hour >= 7 && hour < 13)
			saluto = "Buongiorno";
		else if (hour >= 13 && hour < tramonto)
			saluto = "Buon pomeriggio";
		else if (hour >= tramonto && hour < 23)
			saluto = "Buonasera";
		else
			saluto = "Buonanotte";
		
		return saluto;
	} // fine getSaluto()
	
	/** Metodo che restituisce il nome del mese a partire dal suo numero. Esempio:<br>
	 * <table><tr><th>Numero</th><th>Mese</th></tr><tr><td>1</td><td>gennaio</td></tr>
	 *     <tr><td>2</td><td>febbraio</td></tr>
	 *     <tr><td>3</td><td>marzo</td></tr>
	 *     <tr><td>...</td><td>...</td></tr>
	 *     <tr><td>12</td><td>dicembre</td></tr>
	 * </table>
	 * @param mese intero corrispondente al mese.
	 * @return il nome del mese per iscritto in italiano.*/
	public static String getMese(int mese)
	{
		return switch (mese)
		{
			case 1 -> "gennaio";
			case 2 -> "febbraio";
			case 3 -> "marzo";
			case 4 -> "aprile";
			case 5 -> "maggio";
			case 6 -> "giugno";
			case 7 -> "luglio";
			case 8 -> "agosto";
			case 9 -> "settembre";
			case 10 -> "ottobre";
			case 11 -> "novembre";
			case 12 -> "dicembre";
			
			default -> throw new IllegalStateException("Unexpected value: " + mese);
		};
	} // fine getMese()
	
	
	/**Restituisce un calendario gregoriano localizzato in Italia, a Roma.
	 * @return a GregorianCalendar object*/
	public static GregorianCalendar getLocalizedCalendar()
	{
		final TimeZone roma = TimeZone.getTimeZone("Europe/Rome");
		return new GregorianCalendar(roma, Locale.ITALY);
	} // fine getCurrentTime()
	
	
	/**Metodo per effettuare richieste http
	 * @param websiteAddress l'indirizzo URL del sito da contattatare
	 * @return <code>JsonObject</code> della risposta <br/><code>null</code> in caso di fallimento
	 * @see JsonObject
	 * @see JsonElement
	 * */
	public static JsonElement httpRequest(String websiteAddress)
	{
		if (websiteAddress == null || websiteAddress.isBlank()) { return null; }
		
		final HttpURLConnection connection;
		try
		{
			final URL url = URI.create(websiteAddress).toURL();
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			
			final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			final StringBuilder response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			final HashMap<String, String> htmlCodes = htmlCodes();
			final String newString = replaceEntities(response.toString(), htmlCodes);
			// System.out.printf("\n\n\nRAW RESPONSE:\n%s\n\nSTRING WITHOUT HTML CODES:\n%s\n\n\n", response, newString);
			return JsonParser.parseString(newString);
			
		}catch (Exception e) { new Error<Exception>().print(Utilities.class, e); }
		
		return null;
		
	}
	
	/**@param s la stringa in input
	 * @param map la map contenente gli elementi da sostituire nella stringa
	 * @return la stringa con gli elementi sostituiti*/
	public static String replaceEntities(String s, Map<String, String> map)
	{
		for (Map.Entry<String, String> entry : map.entrySet())
		{
			s = s.replace(entry.getKey(), entry.getValue());
		}
		return s;
	}
	
	private static HashMap<String, String> htmlCodes()
	{
		final HashMap<String, String> htmlCodes = new HashMap<>();
		htmlCodes.put("&amp;", "&");
		htmlCodes.put("&quot;", "'");
		htmlCodes.put("&#039;", "'");
		htmlCodes.put("&rsquo;", "'");
		htmlCodes.put("&nbsp;", " ");
		
		htmlCodes.put("&agrave;", "à");
		htmlCodes.put("&aacute;", "á");
		
		htmlCodes.put("&egrave;", "è");
		htmlCodes.put("&eacute;", "é");
		
		htmlCodes.put("&igrave;", "ì");
		htmlCodes.put("&iacute;", "í");
		
		htmlCodes.put("&ograve;", "ò");
		htmlCodes.put("&oacute;", "ó");
		
		htmlCodes.put("&ugrave;", "ù");
		htmlCodes.put("&uacute;", "ú");
		
		
		return htmlCodes;
	}
	
	public static String[] divideString(String s)
	{
		final int MAX = 1999;
		if (s.length() < MAX)
			return new String[]{s};
		
		final ArrayList<String> list = new ArrayList<>();
		
		for (int i = 0; i < s.length(); i += MAX)
		{
			list.add(s.substring(i, Math.min(i+MAX, s.length())));
		}
		
		return list.toArray(new String[0]);
	}
	
	
} // fine classe Utilities
