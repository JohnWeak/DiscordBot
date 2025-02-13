import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreadQuiz extends Thread
{
	private final SlashCommandInteractionEvent event;
	@Getter private static String answer;
	
	public ThreadQuiz(SlashCommandInteractionEvent event)
	{
		this.event = event;
	}
	
	@Override
	public void run()
	{
		final EmbedBuilder embed = new EmbedBuilder();
		final JsonObject j;
		final String url = "https://opentdb.com/api.php?amount=1";
		
		j = Utilities.httpRequest(url);
		
		final JsonArray jsonArray = j.getAsJsonArray("results");
		final JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
		
		final String question = jsonObject.getAsJsonObject().get("question").getAsString();
		final JsonElement correctAnswer = jsonObject.getAsJsonObject().get("correct_answer");
		final JsonArray incorrectAnswers = jsonObject.getAsJsonArray("incorrect_answers");
		answer = correctAnswer.getAsString();
		
		final List<JsonElement> allAnswers = new ArrayList<>();
		allAnswers.add(correctAnswer);
		allAnswers.addAll(incorrectAnswers.asList());
		
		Collections.shuffle(allAnswers);
		
		final ArrayList<Button> buttons = new ArrayList<>();
		final ActionRow actionRow;
		final StringBuilder sb = new StringBuilder();
		
		embed.setTitle("test");
		embed.setColor(Color.RED);
		sb.append(question).append("\n");
		for (int i = 0; i < allAnswers.size(); i++)
		{
			buttons.add(Button.primary(String.valueOf(i), allAnswers.get(i).getAsString()));
			sb.append(String.format("%d) %s\n", i+1, allAnswers.get(i).getAsString()));
		}
		
		embed.setDescription(sb.toString());
		embed.addField("Category", jsonObject.get("category").getAsString(), true);
		embed.addField("Difficulty", jsonObject.get("difficulty").getAsString(), true);
		embed.addField("Type", jsonObject.get("type").getAsString(), true);
		
		actionRow = ActionRow.of(buttons);
		
		event
			.replyEmbeds(embed.build())
			.setComponents(actionRow)
		.queue(l ->
		{
			final Timer timer = new Timer(true);
			final ButtonListener listener = new ButtonListener();
			final int timeout = 3 * 60 * 1000;
			
			event.getJDA().addEventListener(listener);
			timer.schedule(new RemoveListenerTask(event, l, embed, actionRow), timeout);
			
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