import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public
class ButtonListener extends ListenerAdapter
{
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
	{
		final String m = getString(event);
		
		try {
			event.deferReply(true).queue(v -> {
			event.getHook().sendMessage(m).queue();
			event.getInteraction().getChannel().editMessageComponentsById(
				event.getMessageId(),
				event.getMessage().getActionRows().stream()
					.map(ActionRow::asDisabled)
					.collect(Collectors.toList())
				).queue();
			});
		} catch (IllegalStateException e) {
			new Error<Exception>().print(this, e);
		}
	}
	
	@NotNull
	private String getString(@NotNull ButtonInteractionEvent event)
	{
		final String label = event.getButton().getLabel().toLowerCase();
		final Member member = event.getMember();
		final String author = member == null ? "" : member.getUser().getName();
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
