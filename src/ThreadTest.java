import net.dv8tion.jda.api.entities.TextChannel;

public class ThreadTest extends Thread
{
	private final String msg;
	private final TextChannel tc;
	
	public ThreadTest(String msg, TextChannel tc)
	{
		this.msg = msg;
		this.tc = tc;
	}
	
	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(2000);
			tc.sendMessage(msg).queue();
		}
		catch (InterruptedException e)
		{
			Error.print(ThreadTest.class, e);
		}
		
	}
}
