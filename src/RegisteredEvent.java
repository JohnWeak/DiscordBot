import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RegisteredEvent(String description, LocalDateTime timeItHappened)
{
	@Override
	public String toString()
	{
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
		return String.format("Event `%s` happened at `%s`\n", description, timeItHappened.format(formatter));
	}
}