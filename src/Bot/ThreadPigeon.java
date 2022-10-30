package Bot;

public class ThreadPigeon extends Thread
{
	public void run()
	{
		new Commands().pigeonBazooka();
	}
	
} // fine Bot.ThreadPigeon