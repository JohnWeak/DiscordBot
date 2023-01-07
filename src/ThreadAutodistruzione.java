import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ThreadAutodistruzione extends Thread
{
	private final Message message;
	private JDA jda;
	private TextChannel tc;
	
	public ThreadAutodistruzione(Message message)
	{
		this.message = message;
		jda = message.getJDA();
		tc = message.getTextChannel();
	} // fine costruttore
	
	
	@Override
	public void run()
	{
		short seconds = 5;
			tc.sendMessage("" + seconds).queue(l ->
			{
				for (short i = seconds; i > 0; i--)
				{
					l.editMessage("" + i).queue();
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						tc.sendMessage("" + e).queue();
					}
				}
				l.delete().queue();
			});
		
		
	} // fine run()
	
} // fine ThreadAutodistruzione
