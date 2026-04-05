import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ThreadTris extends Thread
{
	private final MessageChannel channel;

	public ThreadTris(@NotNull MessageChannel channel)
	{
		this.channel = channel;
	}

	@Override
	public void run() 
	{
		final ArrayList<ItemComponent> buttons = new ArrayList<>();
		Button b;
		for (int i = 0; i < 9; i++)
		{
			b = Button.secondary(""+i, ""+(i+1));
			buttons.add(b);
		}

		channel
			.sendMessage("Prova sa sa prova")
			.addActionRow(buttons.subList(0, 3))
			.addActionRow(buttons.subList(3, 6))
			.addActionRow(buttons.subList(6, 9))
		.queue();

	}
}
