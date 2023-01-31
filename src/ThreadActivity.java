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
		// 1000*60*60 : 3600000, cioè 1 ora
		final int ora = 3600000;
		
		var timeToSleep = (new Random().nextInt(ora, ora*2));
		var colpevole = (Commands.author == null || Commands.authorName.isEmpty() ? "il governo" : Commands.authorName);
		
		while (true)
		{
			if (!keepGoing)
				return;
			
			
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
