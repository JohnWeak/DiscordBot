import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public
class ButtonListener extends ListenerAdapter
{
	private final ThreadQuiz tq;
	public ButtonListener(ThreadQuiz tq)
	{
		this.tq = tq;
	}
	
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
	{
		final String m = getString(event);
		final String correctAnswer = ThreadQuiz.getAnswer();
		
		try
		{
			event.reply(m).queue(l -> tq.setActive(false));
			final String clickedButtonId = event.getButton().getId();
			
			final List<ActionRow> updatedRows = event.getMessage().getActionRows().stream()
				.map(row ->
				{
					final List<Button> updatedButtons = row.getButtons().stream()
						.map(button ->
						{
							if (button.getId() != null && button.getId().equals(clickedButtonId))
							{
								return (button.getLabel().equals(correctAnswer) ?
									button.asDisabled().withStyle(ButtonStyle.SUCCESS) :
									button.asDisabled().withStyle(ButtonStyle.DANGER)
								);
							}
							else
							{
								return button.asDisabled();
							}
						})
						.collect(Collectors.toList());
						
						return ActionRow.of(updatedButtons);
				}).collect(Collectors.toList());
			
			event.getInteraction().getChannel()
				.editMessageComponentsById(event.getMessageId(), updatedRows)
				.queue();
		} catch (IllegalStateException e) { new Error<Exception>().print(this, e); }
	}
	
	
	@NotNull
	private String getString(@NotNull ButtonInteractionEvent event)
	{
		final String label = event.getButton().getLabel().toLowerCase();
		final Member member = event.getMember();
		final String author = member == null ? "" : member.getEffectiveName();
		final String userAnswer = event.getButton().getLabel();
		final String correctAnswer = ThreadQuiz.getAnswer();
		final String m;
		
		if (label.contains("true") || label.contains("false"))
		{
			m = event.getButton().getLabel().equals(ThreadQuiz.getAnswer()) ?
				String.format("Correct! It is %s.\n -# %s", correctAnswer, author) :
				String.format("Wrong, it was %s.\n -# %s", correctAnswer, author);
		}
		else
		{
			m = event.getButton().getLabel().equals(ThreadQuiz.getAnswer()) ?
				String.format("Correct! The answer was \"%s\".\n-# %s", userAnswer, author) :
				String.format("\"%s\" is wrong. The correct answer was: \"%s\".\n-# %s", userAnswer, correctAnswer, author);
			
		}
		return m;
	}
}
