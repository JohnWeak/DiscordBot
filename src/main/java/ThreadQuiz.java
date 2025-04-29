import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreadQuiz extends Thread
{
	private final SlashCommandInteractionEvent event;
	
	@Getter
	@Setter
	private volatile boolean active = true;
	
	@Getter
	private static String answer;
	
	public ThreadQuiz(SlashCommandInteractionEvent event)
	{
		this.event = event;
	}
	
	@Override
	public void run()
	{
		final EmbedBuilder embed = new EmbedBuilder();
		JsonObject j;
		final String url = "https://opentdb.com/api.php?amount=1";
		final String category, difficulty, type;
		
		try
		{
			int seconds = 1;
			do
			{
				j = Utilities.httpRequest(url).getAsJsonObject();
				if (j == null)
				{
					Thread.sleep(1000 * (long) seconds);
					if (seconds < 5)
						seconds += 1;
				}
			} while (j == null);
			
			
			final JsonArray jsonArray = j.getAsJsonArray("results");
			final JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
			
			final String question = jsonObject.getAsJsonObject().get("question").getAsString();
			final JsonElement correctAnswer = jsonObject.getAsJsonObject().get("correct_answer");
			final JsonArray incorrectAnswers = jsonObject.getAsJsonArray("incorrect_answers");
			answer = correctAnswer.getAsString();
			
			category = jsonObject.get("category").getAsString();
			difficulty = Utilities.capitalize(jsonObject.get("difficulty").getAsString());
			type = Utilities.capitalize(jsonObject.get("type").getAsString());
			
			
			final List<JsonElement> allAnswers = new ArrayList<>();
			
			if (type.equalsIgnoreCase("boolean"))
			{
				allAnswers.addAll(incorrectAnswers.asList());
				allAnswers.add(correctAnswer);
				
				if (correctAnswer.getAsBoolean())
				{
					Collections.reverse(allAnswers);
				}
			}
			else
			{
				allAnswers.add(correctAnswer);
				allAnswers.addAll(incorrectAnswers.asList());
				Collections.shuffle(allAnswers);
			}
			
			final ArrayList<Button> buttons = new ArrayList<>();
			final ActionRow actionRow;
			final StringBuilder sb = new StringBuilder();
			
			embed.setTitle(question);
			embed.setColor(Color.RED);
			
			for (int i = 0; i < allAnswers.size(); i++)
			{
				final String tempAnswer = allAnswers.get(i).getAsString();
				buttons.add(Button.primary(String.valueOf(i), tempAnswer));
				sb.append(String.format("• %s\n", tempAnswer));
			}
			
			embed.addField("Category", category, true);
			embed.addField("Difficulty", difficulty, true);
			embed.addField("Type",  type, true);
			embed.setDescription(sb.toString());
			
			actionRow = ActionRow.of(buttons);
			
			event.deferReply().queue();
			
			event.getHook()
				.editOriginalEmbeds(embed.build())
				.setComponents(actionRow)
			.queue();
			
			final ButtonListener buttonListener = new ButtonListener(this);
			event.getJDA().addEventListener(buttonListener);
			
		}
		catch (Exception e) { new Errore<Exception>().print(this,e); }
	}
	
}