import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	private final static int max = 898; // pokedex completo
	private static final File nomiPokemon = new File("nomiPokemon.txt");
	private static final Random random = new Random();

	// private static int pokemon_id = 261;
	// https://pokeapi.co/api/v2/pokemon/261/ -> Poochyena
	
	private String nome;
	private String img;
	private boolean shiny = false;
	private String descrizione;
	private String[] tipo = new String[2];
	private String generazione;
	private String dexNumber;
	private String[] lineaEvolutiva;
	
	public Pokemon()
	{
		shiny();
		
		try
		{
			String[] result = generatePokemon(random.nextInt(max)+1);
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
			img = generatePokemon(random.nextInt(max)+1)[1];
		}catch (Exception e) { e.printStackTrace(); }
		
	}

	public Pokemon(String nome, String descrizione, boolean shiny)
	{
		int id = 1;
		this.shiny = shiny;

		this.nome = nome;
		try
		{
			Scanner scanner = new Scanner(nomiPokemon);
			while (scanner.hasNextLine())
				if (nome.equalsIgnoreCase(scanner.nextLine()))
					break;
				else
					id++;


			img = generatePokemon(id)[1];
		}catch (Exception e) { e.printStackTrace(); }

		this.descrizione = descrizione;
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
			String[] result = generatePokemon(random.nextInt(max)+1);
			nome = result[0];
			img = result[1];
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private String[] generatePokemon(int id)
	{
		if (id <= 0)
			id = random.nextInt(max)+1;
		Scanner scanner;
		String[] risultato = new String[2];
		
		try
		{
			scanner = new Scanner(nomiPokemon);
			for (int i = 0; i < id; i++)
				nome = scanner.nextLine();
			
		}catch (FileNotFoundException e) {}
		
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+id+".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+id+".png";
		
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
	public String getDescrizione()
	{
		return descrizione;
	}
	public String[] getTipo()
	{
		return tipo;
	}
	public String getGenerazione()
	{
		return generazione;
	}
	public String getDexNumber()
	{
		return dexNumber;
	}
	public String[] getLineaEvolutiva()
	{
		return lineaEvolutiva;
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
	public void setDescrizione(String descrizione)
	{
		this.descrizione = descrizione;
	}
	public void setTipo(String[] tipi)
	{
		this.tipo = tipi;
	}
	public void setGenerazione(String generazione)
	{
		this.generazione = generazione;
	}
	public void setDexNumber(String dexNumber)
	{
		this.dexNumber = dexNumber;
	}
	public void setLineaEvolutiva(String[] lineaEvolutiva)
	{
		this.lineaEvolutiva = lineaEvolutiva;
	}
} // fine classe Pokemon
