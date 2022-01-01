import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;

public class Pokemon
{
	private final URL urlGetPokemon = new URL("https://pokeapi.co/api/v2/pokemon/"); //  dopo pokemon/ inserire numero_id oppure nome
	private static int pokemon_id = 261;
	// https://pokeapi.co/api/v2/pokemon/261/ -> Poochyena
	
	private String nome;
	private int puntiFerita;
	private boolean isShiny = false;
	
	public Pokemon() throws MalformedURLException
	{
	
	}
	
	
	public String[] requestName() throws IOException
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
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+x+".png";
		System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
		System.out.printf("Nome: %s\tImmagine: %s\n", name, urlImg);
		
		risultato[0] = name;
		risultato[1] = urlImg;
		return risultato;
	
	} // fine requestName()
	
	
} // fine classe Pokemon
