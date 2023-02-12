import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PrivateMessage
{
	private final User user;
	private final MessageChannel messageChannel;
	private static final Object object = PrivateMessage.class;
	
	/**Questa classe permette di inviare messaggi privati agli utenti passati tramite parametro
	 * @param user Utente a cui inviare il messaggio privato. */
	public PrivateMessage(User user)
	{
		this.user = user;
		messageChannel = Main.getJda().getTextChannelsByName(Commands.botChannel,true).get(0);
	} // fine costruttore
	
	/** Invocare questa funzione per inviare un messaggio all'utente designato.
	 * @param content Il messaggio da inviare all'utente. */
	public void send(String content)
	{
		try
		{
			var utente = Main.getJda().retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			new Error<Exception>().print(object, e);
		}
	} // fine metodo send()
	
	
} // fine classe
