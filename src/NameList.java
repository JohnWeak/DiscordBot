import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NameList
{
	private static final File nomiPkmn = new File("nomiPokemon.txt");
	
	private JSONObject requestApi(URL url, String numeroPkmn)
	{
		Object file = null;
		try
		{
			URL newUrl = new URL(url + numeroPkmn);
			
			HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
			file = JSONValue.parse(String.valueOf(response));
			
		}
		catch (Exception e) { new Error<Exception>().print(NameList.class, e); }
		return (JSONObject) file;
	}
	
	public String[] generateNameList(URL url)
	{
		int numeroTotalePokemon = 899;
		
		String[] listaNomi = new String[numeroTotalePokemon];
		JSONObject jsonObject;
		
		for (int i = 1; i < numeroTotalePokemon; i++)
		{
			jsonObject = requestApi(url, String.valueOf(i));
			listaNomi[i-1] = (String) jsonObject.get("name");
		}
		return listaNomi;
	}
	
	
	
	private void generateNamePokemon()
	{
		String[] nomi = null;
		FileWriter fileWriter;
		try
		{
			URL url = new URL("https://pokeapi.co/api/v2/pokemon/");
			
			nomi = new NameList().generateNameList(url);
			fileWriter = new FileWriter(nomiPkmn, true);
			int length = nomi.length;
			for (int i = 0; i < length; i++)
				fileWriter.append(nomi[i]).append("\n");
			
			fileWriter.close();
			
			
		}catch (IOException ignored) {}
		
		if (nomi == null)
			System.out.println("Errore nel recuperare i nomi dei pokemon");
		
	}
	
	
	
	
}