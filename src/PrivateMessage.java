import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PrivateMessage
{
	private final User user;
	private final MessageChannel messageChannel;
	
	/**Questa classe permette di inviare messaggi privati agli utenti passati tramite parametro
	 * @param user Utente a cui inviare il messaggio privato.
	 * @param messageChannel Canale su cui inviare il testo dell'eccezione in caso di errore.
	 */
	public PrivateMessage(User user, MessageChannel messageChannel)
	{
		this.user = user;
		this.messageChannel = messageChannel;
	}
	
	public void send(String content)
	{
		try
		{
			user.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			messageChannel.sendMessage("`"+this.getClass()+"`\n"+e).queue();
		}
	}
}
