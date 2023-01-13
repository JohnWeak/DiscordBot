import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

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
	
	// NOMI
	public static final String NOME_GION = "JOHNWEAK";
	public static final String NOME_ENIGMO = "ENIGMO";
	public static final String NOME_OBITO = "ÒBITO";
	public static final String NOME_LEX = "DIODELAG";
	public static final String NOME_OWOBOT = "OWOBOT";
	public static final String NOME_BOWOT = "BOWOT";
	
	
	/**<h3>Questo metodo restituisce l'oggetto utente (User) a partire dal nome passato come parametro.</h3>
	 * @param nome Nome dell'utente da cercare.
	 * @return Oggetto <code>User</code> contentente l'utente richiesto.<br><code>null</code> in caso di errore.
	 * */
	public static User getUtenteFromName(String nome)
	{
		var jda = Commands.message.getJDA();
		var channel = Commands.message.getChannel();
		var utente = "";
		User user;
		
		try
		{
			utente = switch (nome)
			{
				case NOME_GION -> ID_GION;
				case NOME_ENIGMO -> ID_ENIGMO;
				case NOME_LEX -> ID_LEX;
				case NOME_OBITO -> ID_OBITO;
				case NOME_OWOBOT -> ID_OWOBOT;
				case NOME_BOWOT -> ID_BOWOT;
				
				default -> "dioporco";
			};
			
		    user = jda.retrieveUserById(utente).complete();
			
		}
		catch (Exception e)
		{
			channel.sendMessage(""+e).queue();
			return null;
		}
		return user;
	} // fine getUtente()
	
	/**Prende in input il discriminatore (#0935) e restituisce il nome utente (JohnWeak)
	 * @param discriminator il discriminatore (es: <code>#1234</code>) dell'utente da cercare
	 * @return Il nome dell'utente <code>(Utente.NOME)</code>
	 * */
	public static String getNomeUtente(String discriminator)
	{
		return switch (discriminator)
		{
			case Utente.GION -> Utente.NOME_GION;
			case Utente.ENIGMO -> Utente.NOME_ENIGMO;
			case Utente.LEX -> Utente.NOME_LEX;
			case Utente.OBITO -> Utente.NOME_OBITO;
			case Utente.OWOBOT -> Utente.NOME_OWOBOT;
			case Utente.BOWOT -> Utente.NOME_BOWOT;
			
			default -> "E io che cazzo ne so, scusi";
		};
	}
	
	public static User getUtenteFromID(String id)
	{
		return Commands.message.getJDA().retrieveUserById(id).complete();
	}
	
	
}
