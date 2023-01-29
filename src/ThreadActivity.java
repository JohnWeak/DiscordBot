import java.util.Random;

public class ThreadActivity extends Thread
{
	
	@Override
	public void run()
	{
		// 1000*60*60 : 3600000, cioè 1 ora
		final int ora = 3600000;
		final int maxLoops = 10;
		
		var timeToSleep = (new Random().nextInt(ora)) + 3;
		var colpevole = (Commands.author == null || Commands.authorName.isEmpty() ? "il governo" : Commands.authorName);
		
		for (int i = 0; i < maxLoops; i++)
		{
			try
			{
				Thread.sleep(timeToSleep);
			}
			catch (InterruptedException e)
			{
				Commands.canaleBot.sendMessage("Oh no, "+colpevole+ " ha rotto il thread activity: "+e).queue();
				e.printStackTrace();
			}
			
			Commands.message.getJDA().getPresence().setActivity(Main.selectActivity());
		}
	} // fine run()
	
} // fine ThreadActivity