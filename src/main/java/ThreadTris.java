import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ThreadTris extends Thread
{
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final SlashCommandInteractionEvent event;

	@Getter
	@Setter
	private static volatile boolean active = false;

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

		final ArrayList<ActionRow> rows = new ArrayList<>();
		final ArrayList<Button> temp = new ArrayList<>(3);
		
		for (int i = 0; i < 9; i++) 
		{
			temp.add(Button.secondary("tris" + i, ""+(i + 1)));
			if (temp.size() == 3)
			{
				rows.add(ActionRow.of(new ArrayList<>(temp)));
				temp.clear();
			}
		}

		event.reply("Sfidami a tris!")
			.addComponents(rows)
		.queue();

		scheduler.schedule(() -> 
		{
			setActive(false);
			rows.forEach(r -> r.asDisabled());
			event.getHook().editOriginalComponents(rows).queue();
		}, 60, TimeUnit.SECONDS);
	
	} // run()

}