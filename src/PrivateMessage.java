import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ExecutionException;

public class PrivateMessage
{
	private final User user;
	private final MessageChannel messageChannel;
	private static final Object object = PrivateMessage.class;
	private Message.Attachment attachment;
	
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
		var contentToSend = (content.length() > 2000 ? content.substring(0,1999) : content);
		
		try
		{
			var utente = Main.getJda().retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel -> channel.sendMessage(contentToSend)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			new Error<Exception>().print(object, e);
		}
	} // fine metodo send(content)
	
	
	public void send(String content, Message.Attachment attachment)
	{
		if (attachment == null)
			send(content);
		else
		{
			var utente = Main.getJda().retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel ->
			{
				try
				{
					return channel.sendMessage(content)
						.addFile(attachment.downloadToFile().get(), null);
				} catch (InterruptedException | ExecutionException e)
				{
					throw new RuntimeException(e);
				}
			}).queue(l-> { });
		}
		
		
	} // fine send(content, attachment)
	
	
	public void setAttachment(Message.Attachment attachment)
	{
		this.attachment = attachment;
	}
	
} // fine classe
