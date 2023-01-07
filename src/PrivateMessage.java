import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PrivateMessage <T>
{
	private User user;
	private MessageChannel messageChannel;
	
	public PrivateMessage(User user, MessageChannel messageChannel)
	{
		this.user = user;
		this.messageChannel = messageChannel;
	}
	
	public void send(T content)
	{
		try
		{
			user.openPrivateChannel().flatMap(channel -> channel.sendMessage((CharSequence) content)).queue(l->
			{
			
			});
		}
		catch (Exception e)
		{
			messageChannel.sendMessage(""+e).queue();
		}
	}
}
