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
		final String author = event.getInteraction().getMember() == null ? "" : event.getInteraction().getMember().getUser().getName();
		final String answer = event.getButton().getLabel();
		final String correctAnswer = ThreadQuiz.getAnswer();
		final String m = event.getButton().getLabel().equals(ThreadQuiz.getAnswer()) ?
			String.format("Correct! The answer was \"%s\".\n-# %s", answer, author) :
			String.format("%s is wrong. The correct answer was: \"%s\".\n-# %s", answer, correctAnswer, author);
		
		try
		{
			event.getMessage().reply(m).queue();
			event.getInteraction().getChannel().editMessageComponentsById(
					event.getMessageId(),
					event.getMessage().getActionRows().stream()
							.map(ActionRow::asDisabled)
							.collect(Collectors.toList())
			).queue();
		}catch (IllegalStateException e)
		{
			new Error<Exception>().print(this,e);
		}
	}
}
