public abstract class Sillabe
{
	private final char[] vocali = {'a','e','i','o','u'};
	
	public String dividi(String testo)
	{
		final char[] t = testo.trim().toCharArray();
		for (char c : t)
		{
			for (char v : vocali)
			{
				if (c == v)
				{
				
				}
			}
		}
		
		
		return new String(t);
	}
	
}
