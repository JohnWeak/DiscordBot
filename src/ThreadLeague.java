public class ThreadLeague extends Thread
{
	private static final int max = 168 + 20; // ore di war totali + buffer
	
	public void run()
	{
		// se il clan è in lega, allora controlla l'andamento della war ora per ora
		if (new Commands().isClanInLeague())
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
		}
	} // fine run()
	
} // fine classe ThreadLeague
