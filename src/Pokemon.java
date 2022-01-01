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
	
	private String[] generatePokemon() throws IOException
	{
		String[] risultato = new String[2];
		
		Random random = new Random();
		int x = random.nextInt(500);
		final URL url = new URL("https://pokeapi.co/api/v2/pokemon/"+x);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Accept", "application/json");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		Object file = JSONValue.parse(String.valueOf(response));
		JSONObject jsonObject = (JSONObject) file;
		
		String name = (String) jsonObject.get("name");
		name = name.substring(0,1).toUpperCase(Locale.ROOT) + name.substring(1);
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+x+".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/shiny/pokemon"+x+".png";
		//System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
		//System.out.printf("Nome: %s\tImmagine: %s\n", name, urlImg);
		
		risultato[0] = name;
		if (shiny)
			risultato[1] = urlShinyImg;
		else
			risultato[1] = urlImg;
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
	public void setPokemon_id(String img)
	{
		this.img = img;
	}
	public void setShiny(boolean shiny)
	{
		this.shiny = shiny;
	}
	
	
} // fine classe Pokemon
