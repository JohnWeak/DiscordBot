import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;

public class ThreadQuiz extends Thread
{
	private final SlashCommandInteractionEvent event;
	public ThreadQuiz(SlashCommandInteractionEvent event)
	{
		this.event = event;
	}
	
	@Override
	public void run()
	{
		final EmbedBuilder embed = new EmbedBuilder();
		final String[] options = {"foo", "bar", "baz", "lor"};
		final ArrayList<Button> buttons = new ArrayList<>();
		final ActionRow actionRow;
		
		embed.setTitle("test");
		embed.setColor(Color.RED);
		
		for (int i = 0; i < options.length; i++)
		{
			final String risp = String.format("risposta%d", i+1);
			buttons.add(Button.primary(risp,options[i]));
			embed.addField(risp, options[i], false);
		}
		
		actionRow = ActionRow.of(buttons);
		
		event
			.replyEmbeds(embed.build())
			.setComponents(actionRow)
		.queue();
		
	}
	
	
	
}
