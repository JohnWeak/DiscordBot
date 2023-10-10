import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.*;

/**Classe che contiene metodi utili e non intasare la classe Commands.*/
public abstract class Utilities
{
	/**Prende una stringa e la restituisce con la prima lettera maiuscola.
	 * @param lowerCaseString la stringa con la prima lettera in minuscolo.
	 * @return la stringa con la prima lettera in maiuscolo.*/
	public static String capitalize(String lowerCaseString)
	{
		return lowerCaseString.substring(0,1).toUpperCase() + lowerCaseString.substring(1).toLowerCase();
	}
	
	/**Recupera gli ultimi messaggi dal canale testuale desiderato.
	 * @param channel il canale testuale di cui bisogna prendere i messaggi.
	 * @param debug booleano che manda come messaggio privato il risultato a Gion.
	 * @param amount la quantità di messaggi da recuperare.
	 *
	 * @return la lista dei messaggi nella cronologia. list[0] = ultimo messaggio*/
	public static List<Message> channelHistory(MessageChannel channel, boolean debug, int amount)
	{
		var history = channel.getHistory().retrievePast(amount).complete();
		
		if (debug)
		{
			var pm = new PrivateMessage(Utente.getGion());
			var msg = new StringBuilder();
			int i = 0;
			
			for (Message message : history)
			{
				var auth = message.getAuthor();
				var name = auth.getName();
				var disc = auth.getDiscriminator();
				var m = message.getContentStripped();
				
				msg.append("Messaggio numero ").append(i).append(":\t").append(auth).append(" --- ").append(name).append(" (").append(disc).append("): ").append(m).append("\n");
				i+=1;
			}
			
			pm.send(msg.toString());
		}
		return history;
		
	} // fine metodo channelHistory()
	
	/** Trasforma il testo da normale a parodia simil-CaMeL cAsE
	 * @param msg il testo originale.
	 * @return la stringa originale adesso trasformata.
	 * */
	public static String camelCase(String msg)
	{
		var chars = msg.toCharArray();
		var len = chars.length;
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
		var c = getCurrentTime();
		var saluto = "";
		var hour = c.get(Calendar.HOUR_OF_DAY);
		var month = c.get(Calendar.MONTH);
		short tramonto;
		
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
	
	
	public static GregorianCalendar getCurrentTime()
	{
		var roma = TimeZone.getTimeZone("Europe/Rome");
		return new GregorianCalendar(roma, Locale.ITALY);
	} // fine getCurrentTime()
	
} // fine classe Utilities
