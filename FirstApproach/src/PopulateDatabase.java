import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class PopulateDatabase {

    private static int START_INDEX = 0;
    private static int END_INDEX = 3789414;
	
	public static void main(String[] args) {
	    
	    try {
	    	Connection readConnection = DriverManager.getConnection("jdbc:sqlite:D:\\data\\streams\\searchesDB.db");
	    	Connection writeConnection = DriverManager.getConnection("jdbc:sqlite:D:\\data\\streams\\urldb.db");
	    	
	        Statement readStatement = readConnection.createStatement();
//	        ResultSet resultSet = readStatement.executeQuery("select authorName,urls,hashtags from data limit " + START_INDEX + "," + END_INDEX);
	        ResultSet resultSet = readStatement.executeQuery("select authorName,urls,hashtags from data");
	        int i=START_INDEX;
	        Statement writeStatement = writeConnection.createStatement();
	        int index = 0;
	        String values_urls = "", values_hashtags = "";
	        while (resultSet.next()) {
	        	
	        	System.out.println(" Working on row number: " + i);
	        	if(resultSet.getString("urls") == null)
	        		continue;
	            String [] urls = resultSet.getString("urls").trim().split(" ");
	            
	            for(int j=0; j<urls.length; j++)
	            {
	            	String url = urls[j].trim();
	            	if(url != null && url.matches("^http.*") && !url.contains("\"") && !url.contains("\'") && !url.contains(",") && !url.contains("..."))
	            	{
	            		try
	            		{
	            			if(index == 10000)
	            			{
	            				try
	            				{
	            					System.out.println("inserting at " + i);

	            					if(values_hashtags.length() != 0)
	            					{
	            						values_hashtags = values_hashtags.substring(0, values_hashtags.length() - 1);
	            						writeStatement.executeUpdate("INSERT INTO url_hashtag(hashtag,url) values " + values_hashtags);
	            					}

	            					values_urls = values_urls.substring(0, values_urls.length() - 1);
	            					writeStatement.executeUpdate("INSERT INTO url_user(user,url) values " + values_urls);
	            				}
	            				finally
	            				{
	            					index = 0;
	            					values_urls = "";
	            					values_hashtags = "";
	            				}

            					System.out.println("done inserting " + i);
	            			}
	            			
	            			index++;
	            			values_urls = values_urls + String.format("('%s','%s'),", resultSet.getString("authorName"), url);

	            			// Insert into url hashtag table
	            			if(resultSet.getString("hashtags") != null)
	            			{
	            				String [] hashtags = resultSet.getString("hashtags").trim().split("#");
	            				for(int k=1; k<hashtags.length; k++)
	            				{
	            					values_hashtags = values_hashtags + String.format("('#%s','%s'),", hashtags[k].trim(), url);
	            				}
	            			}
	            		}
	            		catch(SQLException sqlException)
	            		{
	            			System.out.println("error row number " + i);
	            			System.out.println(sqlException);
	            		}
	            	}
	            }
	            
	            System.out.println(" Row number " + i++ + " done!");
	        }
	       
	        try
			{
				System.out.println("inserting at " + i);

				if(values_hashtags.length() != 0)
				{
					values_hashtags = values_hashtags.substring(0, values_hashtags.length() - 1);
					writeStatement.executeUpdate("INSERT INTO url_hashtag(hashtag,url) values " + values_hashtags);
				}

				values_urls = values_urls.substring(0, values_urls.length() - 1);
				writeStatement.executeUpdate("INSERT INTO url_user(user,url) values " + values_urls);
			}
			finally
			{
				index = 0;
				values_urls = "";
				values_hashtags = "";
			}

			System.out.println("done inserting " + i);
		
	        
	        readConnection.close();
	        writeConnection.close();
	        return;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	}

}