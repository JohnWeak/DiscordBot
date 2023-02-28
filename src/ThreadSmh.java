import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Random;

public class ThreadSmh extends Thread
{
	private final Object object = ThreadSmh.class;
	
	private final String smh = "<:" + Emotes.smh + "> ";
	private final int max;
	private final MessageChannel mc;
	private final Random random;
	
	public ThreadSmh(MessageChannel mc)
	{
		this.mc = mc;
		random = new Random();
		max = random.nextInt(2, 10);
	}
	
	@Override
	public void run()
	{
		mc.sendMessage(smh).queue(l->
		{
			String newSmh = smh;
			try
			{
				for (int i = 0; i < max; i++)
				{
					var sleep = random.nextInt(500, 3000);
					l.editMessage(newSmh).queue();
					Thread.sleep(sleep);
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
