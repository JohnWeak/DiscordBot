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
		
		final int minuti = 1000*60*2; // 2 minuti
		int minutesToSleep = (new Random().nextInt(minuti, minuti*2));
		
		int timeToSleep = (new Random().nextInt(ora, ora*2));
		String colpevole = (Commands.author == null || Commands.authorName.isEmpty() ? "il governo" : Commands.authorName);
		PrivateMessage pm = new PrivateMessage(Utente.getGion());
		Activity activity;
		String kp = "`keepGoing="+keepGoing+"`";
		
		pm.send("Sono prima del while."+kp);
		
		while (keepGoing)
		{
			String s = this.getClass()+"\n"+Thread.currentThread()+"\n";
			pm.send(s+"\nSono nel while, prima del try/catch."+kp);
			
			try
			{
				Thread.sleep(minutesToSleep);
				
				activity = Main.selectActivity();
				Commands.message.getJDA().getPresence().setActivity(activity);
				pm.send("Cambio activity in: " + activity.getName());
				
			}
			catch (Exception e)
			{
				Commands.canaleBot.sendMessage("Oh no, "+colpevole+ " ha rotto il thread activity: "+e).queue();
				e.printStackTrace();
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
