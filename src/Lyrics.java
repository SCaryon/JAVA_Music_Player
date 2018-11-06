public class Lyrics
{
	String text;
	int time;
	Lyrics(String text,int time)
	{
		this.text=text;
		this.time=time;
	}
	@Override
	public String toString()
	{
		return text;
	}
}