
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Random;

@Getter
public class Card
{
	// GETTER
	private final String seme;
	private final String valore;
	private final String link;
	
	private static final Random random = new Random();
	private static final String[] semi = {"Cuori", "Quadri", "Fiori", "Picche"}; // Hearts, Diamonds, Clubs, Spades
	private static final String[] valori = {"Asso", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Regina", "Re"};
	
	public static final String[] simboli = {"♥️", "♦️", "♣️", "♠️"};
	
	public Card()
	{
		final String[] linkTemp = {"https://www.deckofcardsapi.com/static/img/", "valore", "seme", ".png"};
		seme = semi[random.nextInt(4)];
		valore = valori[random.nextInt(13)];
		
		linkTemp[1] = switch (valore)
		{
			case "Asso" -> "A";
			case "10" -> "0";
			case "Jack" -> "J";
			case "Regina" -> "Q";
			case "Re" -> "K";
			default -> valore;
		};
		
		linkTemp[2] = switch (seme)
		{
			case "Cuori" ->  "H";     // Hearts
			case "Quadri" ->  "D";     // Diamonds
			case "Fiori" ->  "C";     // Clubs
			case "Picche" ->  "S";     // Spades
			default -> null;
		};
		
		link = String.format("%s%s%s%s", linkTemp[0], linkTemp[1], linkTemp[2], linkTemp[3]);
		
	} // fine costruttore
	
	
	public EmbedBuilder getEmbed()
	{
		final String titolo = titoloCarta();
		final String immagineCartaAPI = linkImmagine();
		final Color color = coloreCarta();
		final String seme = semeCarta();
		
		return new EmbedBuilder()
			.setTitle(titolo)
			.setImage(immagineCartaAPI)
			.setColor(color)
			.setFooter(seme)
		;
		
	} // fine sendCarta
	
	
	/** Restituisce il titolo della carta sotto forma di stringa */
	private String titoloCarta()
	{
		return this.getValore() + " di " + this.getSeme();
	}
	
	/** Restituisce il link dell'immagine della carta sotto forma di stringa */
	private String linkImmagine()
	{
		return this.getLink();
	}
	
	/** Restituisce il colore della carta sotto forma di Color */
	private Color coloreCarta()
	{
		return this.getSeme().equals("Cuori") || this.getSeme().equals("Quadri") ? Color.RED : Color.BLACK;
	}
	
	/** Restituisce il valore del seme della carta sotto forma di stringa */
	private String semeCarta()
	{
		return switch (this.getSeme())
		{
			case "Cuori" -> Card.simboli[0];
			case "Quadri" -> Card.simboli[1];
			case "Fiori" -> Card.simboli[2];
			case "Picche" -> Card.simboli[3];
			default -> null;
		};
	}
	
	
	public int getSemeInt()
	{
		return switch (seme)
		{
			case "Cuori" -> 45;
			case "Quadri" -> 44;
			case "Fiori" -> 43;
			case "Picche" -> 42;
			
			default -> -1;
		};
	}
	
	public int getValoreInt()
	{
		return switch (valore)
		{
			case "Asso" -> 14;
			case "Re" -> 13;
			case "Regina" -> 12;
			case "Jack" -> 11;
			
			default -> Integer.parseInt(valore);
		};
	}
	
	
} // fine classe Card
