import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

public
class ButtonListener extends ListenerAdapter
{
	private final SlashCommandInteractionEvent quiz;
	
	public ButtonListener(SlashCommandInteractionEvent event)
	{
		quiz = event;
	}
	
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
	{
		System.out.printf("event:%s\nquiz:%s\nbutton:%s\n", event, quiz, event.getButton());
	}
}
