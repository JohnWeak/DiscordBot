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
		final int DUPLICATES = 5;
		final StringBuilder eventString = new StringBuilder();
		int index = 1;
		
		if (random.nextInt(69420) == 42)
		{
			eventString.append("Oh no...\n");
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
			eventString.setLength(0);
			return;
		}
		
		eventString
			.append("Here's the recap of the `")
			.append(allEvents.size())
			.append("` disconnection events.\n\n");
		
		for (RegisteredEvent event : allEvents)
		{
			eventString.append(index++).append(") ").append(event.toString());
		}
		
		eventString.append("\nEnd of recap. Enjoy the rest of the day. Or don't, I don't really care.");
		for (User user : usersToNotify)
		{
			new PrivateMessage(user).send(eventString.toString());
		}
		
		allEvents.clear();
	}
	
	
	public void addEvent(RegisteredEvent event)
	{
		allEvents.add(event);
	}
	
}
