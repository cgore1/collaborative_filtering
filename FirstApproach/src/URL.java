import java.util.ArrayList;
import java.util.List;

public class URL {
	
	public String url;
	public List<String> users;
	public List<String> hashTags;
	
	public URL(String url) 
	{
		this.url = url;
		users = new ArrayList<String>();
		hashTags = new ArrayList<String>();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof URL)
		{
			return url.equals(((URL)other).url);
		}
		return false;
	}
}
