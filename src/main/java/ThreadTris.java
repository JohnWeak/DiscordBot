import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ThreadTris extends Thread
{
	private final SlashCommandInteractionEvent event;
	
	@Getter
	@Setter
	private volatile boolean active = true;

	public ThreadTris(@NotNull SlashCommandInteractionEvent event)
	{
		this.event = event;
	}

	@Override
	public void run() 
	{
		if (isActive())
		{
			event.reply("C'è già un gioco del tris attivo.").queue();
			return;
		}

		final ArrayList<ItemComponent> buttons = new ArrayList<>();
		Button b;
		for (int i = 0; i < 9; i++)
		{
			b = Button.secondary("tris"+i, ""+(i+1));
			buttons.add(b);
		}

		event.reply("")
			.addActionRow(buttons.subList(0, 3))
			.addActionRow(buttons.subList(3, 6))
			.addActionRow(buttons.subList(6, 9))
		.queue();

	}
}