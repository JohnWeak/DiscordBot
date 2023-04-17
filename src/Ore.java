public class Ore
{
	private final String[] numeriParole =
	{
		"mezzanotte", "una", "due", "tre", "quattro", "cinque", "sei", "sette", "otto", "nove", "dieci",
		"undici", "dodici", "tredici", "quattordici", "quindici", "sedici", "diciassette", "diciotto",
		"diciannove", "venti", "ventuno", "ventidue", "ventitré", "ventiquattro",
		"venticinque", "ventisei", "ventisette", "ventotto", "ventinove", "trenta", "trentuno", "trentadue",
		"trentatré", "trentaquattro", "trentacinque", "trentasei", "trentasette", "trentotto", "trentanove",
		"quaranta", "quarantuno", "quarantadue", "quarantatré", "quarantaquattro", "quarantacinque", "quarantasei",
		"quarantasette", "quarantotto", "quarantanove", "cinquanta", "cinquantuno", "cinquantadue", "cinquantatré",
		"cinquantraquattro", "cinquantacinque", "cinquantasei", "cinquantasette", "cinquantotto", "cinquantanove"
	};
	private int ore, minuti;
	
	public Ore(int ore, int minuti)
	{
		// se l'orario è compreso fra 0:00 e 23:59 tutto ok
		if (ore > -1 && minuti > -1 && ore < 24 && minuti < 60)
		{
			this.ore = ore;
			this.minuti = minuti;
		}
		else
			new Error<String>().print(this,"Errore! Ore o minuti sbagliati.");
	}
	
	
	//GETTER
	public String getOra()
	{
		return numeriParole[ore];
	}
	
	public String getMinuti()
	{
		return (minuti == 0 ? "" : numeriParole[minuti]);
	}
	
	
	//SETTER
	public void setOre(int ore)
	{
		if (ore > -1 && ore < 25)
			this.ore = ore;
	}
	
	public void setMinuti(int minuti)
	{
		if (minuti > -1 && minuti < 60)
			this.minuti = minuti;
	}
	
	
} // fine classe Ore
