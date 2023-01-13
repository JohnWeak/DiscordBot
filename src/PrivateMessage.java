import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PrivateMessage
{
	private final User user;
	private MessageChannel messageChannel;
	
	/**Questa classe permette di inviare messaggi privati agli utenti passati tramite parametro
	 * @param user Utente a cui inviare il messaggio privato.
	 */
	public PrivateMessage(User user)
	{
		this.user = user;
		setBotChannel();
	}
	
	public void send(String content)
	{
		try
		{
			var utente = messageChannel.getJDA().retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			messageChannel.sendMessage("`"+this.getClass()+"`\n"+e).queue();
		}
	}
	
	private void setBotChannel()
	{
		messageChannel = Commands.message.getJDA().getTextChannelsByName(Commands.botChannel,true).get(0);
	}
}
