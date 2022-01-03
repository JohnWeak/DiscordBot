import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Pokemon
{
	private final static int max = 898; // pokedex completo
	
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
		String[] risultato = new String[2];
		JSONObject jsonObject;
		Random random = new Random();
		int x = random.nextInt(max);
		
		jsonObject = requestApi(x);
		
		String name = (String) jsonObject.get("name");
		nome = name.substring(0,1).toUpperCase(Locale.ROOT) + name.substring(1);
		
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
	
	private JSONObject requestApi(int numeroPkmn)
	{
		Object file = null;
		try
		{
			final URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + numeroPkmn);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
			file = JSONValue.parse(String.valueOf(response));
			
		}
		catch (IOException e) { System.out.println("Errore nel mandare la richiesta di nomi con API"); }
		return (JSONObject) file;
	}
	
	public String[] generateNameList()
	{
		int numeroTotalePokemon = 899;
		String[] listaNomi = new String[numeroTotalePokemon];
		JSONObject jsonObject;
		
		for (int i = 1; i < numeroTotalePokemon; i++)
		{
			jsonObject = requestApi(i);
			listaNomi[i-1] = (String) jsonObject.get("name");
		}
		return listaNomi;
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
