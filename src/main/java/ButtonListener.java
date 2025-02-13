import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public
class ButtonListener extends ListenerAdapter
{
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
	{
		final String m = event.getButton().getLabel().equals(ThreadQuiz.getAnswer()) ? "Correct Answer!" :
			String.format("Wrong! The correct answer was: \"%s\".", ThreadQuiz.getAnswer());
		
		event.reply(m).queue();
		List<ActionRow> actionRows = event.getMessage().getActionRows();
		event.getInteraction().getChannel().editMessageComponentsById(
		event.getId(),
		actionRows.stream()
				.map(ActionRow::asDisabled)
				.collect(Collectors.toList())
		).queue();

	}
}
