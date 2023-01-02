import net.dv8tion.jda.api.entities.User;

public class PrivateMessage <T>
{
	private User user;
	
	public PrivateMessage(User user)
	{
		this.user = user;
	}
	
	public void send(T content)
	{
		user.openPrivateChannel().flatMap(channel -> channel.sendMessage((CharSequence) content)).queue(l->
		{
		
		});
	}
}
