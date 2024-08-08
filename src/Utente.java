import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Utente
{
	private static final Object object = Utente.class;
	
	// ID
	public static final String ID_GION = "180759114291478528";
	public static final String ID_ENIGMO = "222014453741256705";
	public static final String ID_OBITO = "221724287705415680";
	public static final String ID_LEX = "379675691534516224";
	public static final String ID_OWOBOT = "408785106942164992";
	public static final String ID_BOWOT = "836586862213726228";
	
	// NOMI
	public static final String NOME_JOHN = "JOHNWEAK_";
	public static final String NOME_JOHN2 = "JOHN_WEAK";
	public static final String NOME_JOHN3 = "JOHN WEAK";
	public static final String NOME_ENIGMO = "ENIGMO";
	public static final String NOME_OBITO = "ÒBITO";
	public static final String NOME_OBITO2 = "OBITO";
	public static final String NOME_LEX = "DIODELAG";
	public static final String NOME_OWOBOT = "OWOBOT";
	public static final String NOME_BOWOT = "BOWOT";
	
	
	/**<h3>Questo metodo restituisce l'oggetto utente (User) a partire dal nome passato come parametro.</h3>
	 * @param nome Nome dell'utente da cercare.
	 * @return Oggetto <code>User</code> contentente l'utente richiesto.<br><code>null</code> in caso di errore.
	 * */
	public static User getUtenteFromName(String nome)
	{
		final JDA jda = Main.getJda();
		final MessageChannel channel = Commands.message.getChannel();
		String utente = "";
		final User user;
		
		try
		{
			utente = switch (nome.toUpperCase())
			{
				case NOME_JOHN -> ID_GION;
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
			new Error<Exception>().print(object, e);
			return null;
		}
		return user;
	} // fine getUtenteFromName()
	
	public static User getUtenteFromID(String id)
	{
		return Commands.message.getJDA().retrieveUserById(id).complete();
	}
	
	public static User getGion()
	{
		return Main.getJda().retrieveUserById(Utente.ID_GION).complete();
	} // fine getGion()
	
	public static User getEnigmo()
	{
		return Main.getJda().retrieveUserById(Utente.ID_ENIGMO).complete();
	}
	
} // fine classe Utente
