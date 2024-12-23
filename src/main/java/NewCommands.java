import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class NewCommands extends ListenerAdapter
{
	
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		System.out.println(event.getMessage().getContentDisplay());
	}
}
