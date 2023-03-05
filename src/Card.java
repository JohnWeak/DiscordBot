import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Card
{
	private String seme;
	private String valore;
	private String link;
	
	private static final Random random = new Random();
	private static final String[] semi = {"Cuori", "Quadri", "Fiori", "Picche"}; // Hearts, Diamonds, Clubs, Spades
	private static final String[] valori = {"Asso", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Regina", "Re"};
	
	public static final String[] simboli = {"♥️", "♦️", "♣️", "♠️"};
	
	public Card()
	{
		String[] linkTemp = {"https://www.deckofcardsapi.com/static/img/", "valore", "seme", ".png"};
		seme = semi[random.nextInt(4)];
		valore = valori[random.nextInt(13)];
		
		switch (valore)
		{
			case "Asso" -> linkTemp[1] = "A";
			case "10" -> linkTemp[1] = "0";
			case "Jack" -> linkTemp[1] = "J";
			case "Regina" -> linkTemp[1] = "Q";
			case "Re" -> linkTemp[1] = "K";
			default -> linkTemp[1] = valore;
		}
		switch (seme)
		{
			case "Cuori"    ->  linkTemp[2] = "H";     // Hearts
			case "Quadri"   ->  linkTemp[2] = "D";     // Diamonds
			case "Fiori"    ->  linkTemp[2] = "C";     // Clubs
			case "Picche"   ->  linkTemp[2] = "S";     // Spades
		}
		
		link = linkTemp[0] + linkTemp[1] + linkTemp[2] + linkTemp[3];
		
	} // fine costruttore
	
	
	
	
	public void sendCarta(Card carta)
	{
		final var titolo = titoloCarta(carta);
		final var immagineCartaAPI = linkImmagine(carta);
		final var color= coloreCarta(carta);
		final var seme = semeCarta(carta);
		var embed = new EmbedBuilder()
			.setTitle(titolo)
			.setImage(immagineCartaAPI)
			.setColor(color)
			.setFooter(seme);
		
		Commands.channel.sendMessageEmbeds(embed.build()).queue();
		
	} // fine sendCarta
	
	
	/** Restituisce il titolo della carta sotto forma di stringa */
	private String titoloCarta(Card carta)
	{
		return carta.getValoreString() + " di " + carta.getSeme();
	}
	
	/** Restituisce il link dell'immagine della carta sotto forma di stringa */
	private String linkImmagine(Card carta)
	{
		return carta.getLink();
	}
	
	/** Restituisce il colore della carta sotto forma di Color */
	private Color coloreCarta(Card carta)
	{
		return carta.getSeme().equals("Cuori") || carta.getSeme().equals("Quadri") ? Color.red : Color.black;
	}
	
	/** Restituisce il valore del seme della carta sotto forma di stringa */
	private String semeCarta(Card carta)
	{
		return switch (carta.getSeme())
		{
			case "Cuori" -> Card.simboli[0];
			case "Quadri" -> Card.simboli[1];
			case "Fiori" -> Card.simboli[2];
			case "Picche" -> Card.simboli[3];
			default -> null;
		};
	}
	
	
	// GETTER
	public String getSeme()
	{
		return seme;
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
	public String getValoreString()
	{
		return valore;
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
	public String getLink()
	{
		return link;
	}
	
	// SETTER
	public void setSeme(String seme)
	{
		this.seme = seme;
	}
	public void setValore(String valore)
	{
		this.valore = valore;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	
	
	
} // fine classe Card
