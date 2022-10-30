package Bot;

import java.util.Random;

public class SchedaPersonaggio
{
	private String nome;
	private int STR;
	private int DEX;
	private int CON;
	private int INT;
	private int WIS;
	private int CHA;
	
	public SchedaPersonaggio() { }
	
	// GETTER
	public String getNome()
	{
		return nome;
	}
	public int getSTR()
	{
		return STR;
	}
	public int getDEX()
	{
		return DEX;
	}
	public int getCON()
	{
		return CON;
	}
	public int getINT()
	{
		return INT;
	}
	public int getWIS()
	{
		return WIS;
	}
	public int getCHA()
	{
		return CHA;
	}
	
	// SETTER
	public void setNome(String nome)
	{
		this.nome = nome;
	}
	public void setSTR(int STR)
	{
		this.STR = STR;
	}
	public void setDEX(int DEX)
	{
		this.DEX = DEX;
	}
	public void setCON(int CON)
	{
		this.CON = CON;
	}
	public void setINT(int INT)
	{
		this.INT = INT;
	}
	public void setWIS(int WIS)
	{
		this.WIS = WIS;
	}
	public void setCHA(int CHA)
	{
		this.CHA = CHA;
	}
	
	
	public int modifier(int score)
	{
		if (score < 0 || score > 30)
			return -42;
		
		// To determine an ability modifier without consulting the table
		// subtract 10 from the ability score and then divide the total by 2 (round down).
		
		return Math.floorDiv(score-10, 2);
	}
	
	
	private int rollAbility()
	{
		var random = new Random();
		var x = 0;
		for (var i = 0; i < 3; i++)
			x += random.nextInt(6) +1;
		
		return x;
	}
	
	// Rolla 3 D6 un totale di 6 volte per i punteggi abilità
	
} // fine classe Bot.SchedaPersonaggio