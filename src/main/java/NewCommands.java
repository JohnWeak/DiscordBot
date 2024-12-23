import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class NewCommands extends ListenerAdapter
{
	
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		final var a = event.getMessage().getContentDisplay();
		final var b = event.getChannel().getName();
		final var c = event.getGuildChannel().getName();
		
		final String reply = a.concat("\n").concat(b).concat("\n").concat(c).concat("\n").concat(String.valueOf(event.getAuthor().isBot()));
		
		System.out.println(reply);
		
		// event.getChannel().sendMessage(reply).queue();
	}
}
