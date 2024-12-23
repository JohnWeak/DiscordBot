import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NewCommands extends ListenerAdapter
{
	
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		final ArrayList<String> args = new ArrayList<>();
		final StringBuilder sb = new StringBuilder();
		
		if (event.getAuthor().isBot())
			return;
		
		args.add(event.getMessage().getContentDisplay());
		args.add(event.getChannel().getName());
		args.add(event.getAuthor().getName());
		args.add(String.valueOf(event.getAuthor().isBot()));
		
		for (String s : args)
			sb.append(s).append("\n");
		
		final String reply = sb.toString();
		
		System.out.println(reply);
		
		// event.getChannel().sendMessage(reply).queue();
	}
}
