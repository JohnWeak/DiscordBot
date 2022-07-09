public class ThreadLeague extends Thread
{
	private static final int max = 3;
	
	public void run()
	{
		for (int i = 0; i < max; i++)
		{
			new Commands().clashWarLeague(true);
			try
			{
				Thread.sleep(3600000);
			} catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
		
	} // fine run()
	
} // fine classe ThreadLeague
