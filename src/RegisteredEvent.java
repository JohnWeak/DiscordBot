import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**This class represents an event in the JDA. It has a description and the time it happened.
 * @param description a string describing the event.
 * @param timeItHappened a LocalDateTime to keep track of when the event occurred.
 *
 * @see String
 * @see LocalDateTime
 * */
public record RegisteredEvent(String description, LocalDateTime timeItHappened)
{
	/** This Overridden toString() converts the event in a string with details about it
	 * @return a string detailing the event and the time it occurred
	 * @see RegisteredEvent
	 * */
	@Override
	public String toString()
	{
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
		return String.format("`%s` happened at `%s`\n", description.trim(), timeItHappened.format(formatter));
	}
}
