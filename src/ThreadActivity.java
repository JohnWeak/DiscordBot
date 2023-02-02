import net.dv8tion.jda.api.entities.Activity;

import java.util.Random;

public class ThreadActivity extends Thread
{
	private boolean keepGoing;
	
	/**Questo Thread continuerà a cambiare l'attività del bot ogni 1-2 ore.
	 * Per interrompere il thread bisogna impostare <code>keepGoing</code> a <code>false</code>.<br>
	 * Così facendo il thread eseguirà un'ultima volta prima di terminare, senza lanciare eccezioni.
	 */
	public ThreadActivity(boolean keepGoing)
	{
		this.keepGoing = keepGoing;
	}
	
	@Override
	public void run()
	{
		// 1000*60*60 = 3600000 ms = 1 ora
		final int ora = 3600000;
		
		//final int minuti = 1000*60*2; // 2 minuti
		//int minutesToSleep = (new Random().nextInt(minuti, minuti*2));
		
		int timeToSleep;
		String colpevole = (Commands.author == null || Commands.authorName.isEmpty() ? "il governo" : Commands.authorName);
		Activity activity;
		Random random = new Random();
		
		while (keepGoing)
		{
			timeToSleep = (random.nextInt(ora, ora*2));
			try
			{
				Thread.sleep(timeToSleep);
				
				activity = Main.selectActivity();
				Main.getJda().getPresence().setActivity(activity);
				//pm.send("Cambio activity in: " + activity.getName());
				
			}
			catch (Exception e)
			{
				Commands.canaleBot.sendMessage("Oh no, "+colpevole+ " ha rotto il thread activity: "+e).queue();
			}
		}
	} // fine run()
	
	// GETTER
	public boolean isKeepGoing()
	{
		return keepGoing;
	}
	
	// SETTER
	public void setKeepGoing(boolean keepGoing)
	{
		this.keepGoing = keepGoing;
	}
	
} // fine ThreadActivity
