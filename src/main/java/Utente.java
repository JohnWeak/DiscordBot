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
