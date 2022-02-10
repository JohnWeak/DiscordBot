import java.util.Random;

public class Card
{
	private String seme;
	private String valore;
	private static final Random random = new Random();
	private static final String[] semi = {"Cuori", "Quadri", "Fiori", "Picche"}; // Hearts, Diamonds, Clubs, Spades
	private static final String[] valori = {"Asso", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Regina", "Re"};
	
	
	public Card()
	{
		final var semeScelto = semi[random.nextInt(4)];
		final var valoreScelto = valori[random.nextInt(13)];
		
		seme = semeScelto;
		valore = valoreScelto;
	}
	
	public Card(String seme, String valore)
	{
		this.seme = seme;
		this.valore = valore;
	}
	
	
	// GETTER
	public String getSeme()
	{
		return seme;
	}
	public String getValore()
	{
		return valore;
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

} // fine classe Card
