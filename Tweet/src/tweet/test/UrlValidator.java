package tweet.test;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlValidator {
	public static void main(String[] args) {
		UrlValidator validator = new UrlValidator();
		System.out.println(validator.validate("https://ouoashdut.co/9cxARubVxb"));
	}

	private boolean[] validate(String[] urls) {
		
		for(String url : urls)
		{
			validate(url);
		}
	
		return null;
	}

	public boolean validate(String url) {
		try {
//		      HttpURLConnection.setFollowRedirects(false);
		      // note : you may also need
//		      HttpURLConnection.this.setInstanceFollowRedirects(false);
		      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		      con.setRequestMethod("HEAD");
		      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		    }
		    catch (Exception e) {
		       return false;
		    }
	}
}
