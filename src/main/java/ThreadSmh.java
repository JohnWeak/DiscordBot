import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.Random;

public class ThreadSmh extends Thread
{
	private final Object object = ThreadSmh.class;
	
	private final String smh = "<:" + Emotes.smh + "> ";
	private final int MAX;
	private final GuildMessageChannel mc;
	private final Random random;
	
	public ThreadSmh(GuildMessageChannel mc)
	{
		this.mc = mc;
		random = new Random();
		MAX = random.nextInt(2, 10);
	}
	
	@Override
	public void run()
	{
		mc.sendMessage(smh).queue(l->
		{
			String newSmh = smh;
			try
			{
				for (int i = 0; i < MAX; i++)
				{
					l.editMessage(newSmh).queue();
					Thread.sleep(random.nextInt(500, 2000));
					newSmh += smh;
				}
			}
			catch (Exception e)
			{
				new Error<Exception>().print(object, e);
			}
		});
		
	} // fine run()
	
} // fine ThreadSmh
