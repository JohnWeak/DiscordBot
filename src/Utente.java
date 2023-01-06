import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public abstract class Utente
{
	// Numeri (discriminator)
	public static final String GION = "0935";
	public static final String ENIGMO = "7166";
	public static final String OBITO = "2804"; // Òbito, l'accento sulla 'o' è importante
	public static final String LEX = "2241";
	public static final String OWOBOT = "8456"; // bot altrui
	public static final String BOWOT = "5269"; // mio bot
	
	// ID
	public static final String ID_GION = "180759114291478528";
	public static final String ID_ENIGMO = "222014453741256705";
	public static final String ID_OBITO = "221724287705415680";
	public static final String ID_LEX = "379675691534516224";
	public static final String ID_OWOBOT = "408785106942164992";
	public static final String ID_BOWOT = "836586862213726228";
	
	/**<h3>Questo metodo restituisce l'oggetto utente (User) a partire dal nome passato come parametro.</h3>
	 * @param message Oggetto di tipo <code>message</code> necessario per ottenere i dati dal JDA.<br>
	 * @param nome Nome dell'utente da cercare.
	 * @return Oggetto <code>User</code> contentente l'utente richiesto.<br><code>NULL</code> in caso di errore.
	 * */
	public static User getUtente(Message message, String nome)
	{
		var jda = message.getJDA();
		var channel = message.getChannel();
		var utente = "";
		User user;
		try
		{
			utente = switch (nome.toLowerCase())
			{
				case "gion", "giovanni", "john" -> ID_GION;
				case "enigmo", "enigma" -> ID_ENIGMO;
				case "lex", "alex", "rumeno" -> ID_LEX;
				case "obito", "òbito", "óbito", "indiano bastardo" -> ID_OBITO;
				case "owobot" -> ID_OWOBOT;
				case "bowot" -> ID_BOWOT;
				
				default -> "dioporco";
			};
			
			var y = User.fromId(ID_GION);
			
		    jda.retrieveUserById(utente).queue(); // prende l'utente e lo salva in cache
			user = jda.getUserById(utente);
			
		}
		catch (Exception e)
		{
			channel.sendMessage(""+e).queue();
			return null;
		}
		return user;
	} // fine getUtente()
	
}
