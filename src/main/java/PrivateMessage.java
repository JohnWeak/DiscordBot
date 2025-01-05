import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

public class PrivateMessage
{
	private final User user;
	private final JDA jda;
	private static final Object object = PrivateMessage.class;
	
	
	/**Questa classe permette di inviare messaggi privati agli utenti passati tramite parametro
	 * @param user Utente a cui inviare il messaggio privato. */
	public PrivateMessage(User user)
	{
		this.user = user;
		jda = Main.getJda();
		
	}
	
	/** Invocare questa funzione per inviare un messaggio all'utente designato.
	 * @param content Il messaggio da inviare all'utente. */
	public void send(String content)
	{
		final String contentToSend = (content.length() > 2000 ? content.substring(0,1999) : content);
		
		try
		{
			final User utente = jda.retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel -> channel.sendMessage(contentToSend)).queue();
		}
		catch (Exception e)
		{
			new Error<Exception>().print(object, e);
		}
	}
	
	
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
			final User utente = jda.retrieveUserById(user.getId()).complete();
			
			utente.openPrivateChannel().flatMap(channel ->
			{
				try
				{
					return channel.sendMessage(content).addFiles(fileUpload);
				} catch (Exception e)
				{
					new Error<Exception>().print(object, e);
					return null;
				}
			}).queue();
		}
		
		
	}
	
	
} // fine classe
