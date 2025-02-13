import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
		.queue(l ->
		{
			final Timer timer = new Timer(true);
			final ButtonListener listener = new ButtonListener(event);
			final int fiveMinutes = 5 * 60 * 1000;
			
			event.getJDA().addEventListener(listener);
			timer.schedule(new RemoveListenerTask(event, l, embed, actionRow), fiveMinutes);
			
		});
		
	}
	
	
}

class RemoveListenerTask extends TimerTask
{
	private final SlashCommandInteractionEvent event;
	private final EmbedBuilder embed;
	private final InteractionHook hook;
	private final ActionRow actionRow;
	
	public RemoveListenerTask(SlashCommandInteractionEvent event, InteractionHook hook, EmbedBuilder embed, ActionRow actionRow)
	{
		this.event = event;
		this.embed = embed;
		this.hook = hook;
		this.actionRow = actionRow;
	}
	
	@Override
	public void run()
	{
		event.getJDA().removeEventListener(event.getJDA().getRegisteredListeners().getFirst());
		embed.setColor(Color.GRAY);
		
		hook.editOriginalEmbeds(embed.build())
			.setComponents(actionRow.asDisabled())
			.queue();
		
	}
}