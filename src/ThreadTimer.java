import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ThreadTimer extends Thread
{
	private final Message message;
	private JDA jda;
	private TextChannel tc;
	private final int seconds;
	private final User user;
	
	public ThreadTimer(Message message, int seconds, User user)
	{
		this.message = message;
		jda = message.getJDA();
		tc = message.getTextChannel();
		this.seconds = seconds;
		this.user = user;
	} // fine costruttore
	
	
	@Override
	public void run()
	{
		tc.sendMessage("Ok, " + user.getName()+" . Timer impostato.").queue(l ->
		{
			try
			{
				Thread.sleep(seconds);
			} catch (InterruptedException e)
			{
				tc.sendMessage("" + e).queue();
			}
			
			l.editMessage("<@"+user.getIdLong()+">").queue();
		});
		
		
	} // fine run()
	
} // fine ThreadAutodistruzione
