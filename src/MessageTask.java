import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class MessageTask extends TimerTask
{
	private final ArrayList<RegisteredEvent> allEvents = new ArrayList<>();
	private final User[] usersToNotify;
	
	
	public MessageTask(User[] usersToNotify)
	{
		this.usersToNotify = usersToNotify;
	}
	
	@Override
	public void run()
	{
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
