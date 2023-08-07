import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

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
	 * @return la lista dei messaggi nella cronologia.*/
	public static List<Message> channelHistory(TextChannel channel, boolean debug, int amount)
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
	
	
	
} // fine classe Utilities
