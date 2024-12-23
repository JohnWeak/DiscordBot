import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

public class PrivateMessage
{
	private final User user;
	private final GuildMessageChannel messageChannel;
	private static final Object object = PrivateMessage.class;
	@Setter
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
		final String contentToSend = (content.length() > 2000 ? content.substring(0,1999) : content);
		
		try
		{
			final User utente = Main.getJda().retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel -> channel.sendMessage(contentToSend)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			new Error<Exception>().print(object, e);
		}
	} // fine metodo send(content)
	
	
	public void send(String content, Message.Attachment attachment) throws IOException
	{
		if (attachment == null)
		{
			send(content);
		}
		else
		{
			final InputStream inputStream = URI.create(attachment.getUrl()).toURL().openStream();
			final FileUpload fileUpload = FileUpload.fromData(inputStream, Objects.requireNonNull(attachment.getFileExtension())); // Ad esempio, "file.png"
			final User utente = Main.getJda().retrieveUserById(user.getId()).complete();
			utente.openPrivateChannel().flatMap(channel ->
			{
				try
				{
					return channel.sendMessage(content).addFiles(fileUpload);
				} catch (Exception e)
				{
					new Error<Exception>().print(object, e);
					return null;
					// throw new RuntimeException(e);
				}
			}).queue();
		}
		
		
	} // fine send(content, attachment)
	
	
} // fine classe
