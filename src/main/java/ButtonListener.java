import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public
class ButtonListener extends ListenerAdapter
{
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
	{
		final String answer = ThreadQuiz.getAnswer();
		final String m = event.getButton().getLabel().equals(ThreadQuiz.getAnswer()) ?
			String.format("Correct! The answer was \"%s\".", answer) :
			String.format("Wrong! The correct answer was: \"%s\".", answer);

		event.reply(m).queue(l ->
		{
			event.getInteraction().getChannel().editMessageComponentsById(
			event.getMessageId(),
			event.getMessage().getActionRows().stream()
				.map(ActionRow::asDisabled)
				.collect(Collectors.toList())
			).queue();
			
		});
	}
}
