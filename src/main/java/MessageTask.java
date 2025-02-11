import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class MessageTask extends TimerTask
{
	private final ArrayList<RegisteredEvent> allEvents = new ArrayList<>();
	
	public MessageTask() { }
	
	@Override
	public void run()
	{
		final PrivateMessage gion = new PrivateMessage(Utente.getGion());
		final PrivateMessage enigmo = new PrivateMessage(Utente.getEnigmo());
		
		final PrivateMessage[] usersToNotify = new PrivateMessage[]{gion, enigmo};
		final Random random = new Random();
		final int DUPLICATES = 10;
		final StringBuilder eventString = new StringBuilder();
		int index = 1;
		
		if (allEvents.isEmpty())
		{
			eventString.append("No disconnection events found.\n");
		}
		else
		{
			if (random.nextInt(69420) == 42)
			{
				final String MESSAGES_ABOUND = "SIR, THE DAM HAS BEEN BREACHED! THE MESSAGES ARE OVERFLOWING, THE FLOOD IS IMMINENT!\n";
				eventString.append(MESSAGES_ABOUND);
				for (var event : allEvents)
				{
					for (int i = 0; i < DUPLICATES; i++)
					{
						for (PrivateMessage pm : usersToNotify)
						{
							pm.send(event.toString());
						}
					}
				}
				eventString.setLength(0);
				return;
			}
			
			final String recap = String.format("Here's the recap of the `%d` disconnection events\n\n", allEvents.size());
			eventString.append(recap);
			
			for (RegisteredEvent event : allEvents)
			{
				eventString.append(String.format("%d) %s", index++, event.toString()));
			}
			
			final String closer = "\nEnd of recap. Enjoy the rest of the day. Or don't, I don't really care.";
			eventString.append(closer);
		}
		
		for (PrivateMessage pm : usersToNotify)
		{
			pm.send(eventString.toString());
		}
		
		allEvents.clear();
	}
	
	
	public void addEvent(RegisteredEvent event)
	{
		allEvents.add(event);
	}
	
}
