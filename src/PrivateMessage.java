import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class PrivateMessage
{
	private final User user;
	private final MessageChannel messageChannel;
	
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
