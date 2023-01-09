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
	private final String reason;
	
	public ThreadTimer(Message message, int seconds, User user, String reason)
	{
		this.message = message;
		jda = message.getJDA();
		tc = message.getTextChannel();
		this.seconds = seconds;
		this.user = user;
		this.reason = reason;
	} // fine costruttore
	
	
	@Override
	public void run()
	{
		var msg = "Ok, " + user.getName()+". Timer impostato per " + seconds + " secondi.";
		msg += seconds == 69 ? " Nice." : "";
		
		tc.sendMessage(msg).queue(l ->
		{
			try
			{
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e)
			{
				tc.sendMessage("" + e).queue();
			}
			tc.sendMessage("<@"+user.getIdLong()+">\nTimer, timer, timer! È scaduto il tuo timer!").queue();
		});
		
		
	} // fine run()
	
} // fine ThreadTimer
