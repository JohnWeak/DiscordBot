import net.dv8tion.jda.api.entities.User;

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
		final User[] usersToNotify = new User[]{Utente.getGion(), Utente.getEnigmo()};
		final Random random = new Random();
		final int DUPLICATES = 10;
		
		if (random.nextInt(69420) == 42)
		{
			for (var event : allEvents)
			{
				for (int i = 0; i < DUPLICATES; i++)
				{
					for (User u : usersToNotify)
					{
						new PrivateMessage(u).send(event.toString());
					}
				}
			}
			return;
		}
		
		
		final StringBuilder eventString = new StringBuilder();
		for (RegisteredEvent event : allEvents)
		{
			eventString.append(event.toString());
		}
		
		for (User u : usersToNotify)
		{
			new PrivateMessage(u).send(eventString.toString());
		}
		
		allEvents.clear();
	}
	
	
	public void addEvent(RegisteredEvent event)
	{
		allEvents.add(event);
	}
	
}
