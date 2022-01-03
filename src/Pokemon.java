import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	private final static int max = 898; // pokedex completo
	private static final File nomiPokemon = new File("nomiPokemon.txt");
	
	// private static int pokemon_id = 261;
	// https://pokeapi.co/api/v2/pokemon/261/ -> Poochyena
	
	private String nome;
	private String img;
	private boolean shiny = false;
	
	public Pokemon()
	{
		shiny();
		
		try
		{
			String[] result = generatePokemon();
			nome = result[0];
			img = result[1];
		}
		catch (Exception e) { e.printStackTrace(); }
		
	}
	
	public Pokemon(String nome)
	{
		shiny();
		
		this.nome = nome;
		try
		{
			img = generatePokemon()[1];
		}catch (Exception e) { e.printStackTrace(); }
		
	}
	
	public Pokemon(String nome, String img)
	{
		shiny();
		
		this.nome = nome;
		this.img = img;
	}
	
	public Pokemon(boolean shiny)
	{
		this.shiny = shiny;
		try
		{
			String[] result = generatePokemon();
			nome = result[0];
			img = result[1];
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private String[] generatePokemon()
	{
		Scanner scanner;
		String[] risultato = new String[2];
		
		Random random = new Random();
		int x = random.nextInt(max) +1; // 1 -> 898
		
		try
		{
			scanner = new Scanner(nomiPokemon);
			for (int i = 0; i < x; i++)
				nome = scanner.nextLine();
			
		}catch (FileNotFoundException e) {}
		
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+x+".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+x+".png";
		
		risultato[0] = nome;
		
		if (shiny)
			img = urlShinyImg;
		else
			img = urlImg;
		
		risultato[1] = img;
		
		System.out.println(Arrays.toString(risultato)+"\nShiny: "+shiny);
		return risultato;
	
	}
	
	private void shiny()
	{
		if (new Random().nextInt(8192) == 42)
			shiny = true;
	}
	
	
	
	
	//GETTER
	public String getNome()
	{
		return nome;
	}
	public String getImg()
	{
		return img;
	}
	public boolean isShiny()
	{
		return shiny;
	}
	
	//SETTER
	public void setNome(String nome)
	{
		this.nome = nome;
	}
	public void setPokemonId(String img)
	{
		this.img = img;
	}
	public void setShiny(boolean shiny)
	{
		this.shiny = shiny;
	}
	
	
} // fine classe Pokemon
