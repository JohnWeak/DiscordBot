import java.util.Random;

public class Card
{
	private String seme;
	private String valore;
	private String link;
	
	private static final Random random = new Random();
	private static final String[] semi = {"Cuori", "Quadri", "Fiori", "Picche"}; // Hearts, Diamonds, Clubs, Spades
	private static final String[] valori = {"Asso", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Regina", "Re"};
	
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
	
	// GETTER
	public String getSeme()
	{
		return seme;
	}
	public int getSemeInt()
	{
		return switch (seme)
		{
			case "Cuori" -> 50;
			case "Quadri" -> 49;
			case "Fiori" -> 48;
			case "Picche" -> 47;
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
			case "Asso" -> 11;
			case "Jack", "Regina", "Re" -> 10;
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
