package tweet.streams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

public class TweetsParser {
	
	private static LinkExtractor linkExtractor = LinkExtractor.builder().build();
	private static Pattern hashTagPatternMatcher = Pattern.compile("#(\\w+|\\W+)");
	
	/**
	 * Return list of parsed objects in the following order
	 * 		(1) UrlsContainer
	 * 		(2) HashTagsContainer
	 * @param tweetText
	 * @return
	 */
	public List<Object> parseTweets(String tweetText)
	{
		// credits https://github.com/robinst/autolink-java/blob/master/src/test/java/org/nibor/autolink/AutolinkUrlTest.java
		List<Object> parsedValues = new ArrayList<Object>();
		parsedValues.add(new UrlsContainer());
		parsedValues.add(new HashTagsContainer());
		
		if(tweetText != null)
		{
			// URLS
			Iterator<LinkSpan> iterator = linkExtractor.extractLinks(tweetText).iterator();
			while(iterator.hasNext())
			{
				LinkSpan link  = iterator.next();
				if(link.getType().equals(LinkType.URL))
				{
					String url = tweetText.subSequence(link.getBeginIndex(), link.getEndIndex()).toString();
					
					UrlsContainer urlsContainer = (UrlsContainer) parsedValues.get(0);
					urlsContainer.getUrls().add(url);
				}
			}
			
			// HashTags
			Matcher matcher = hashTagPatternMatcher.matcher(tweetText);
			List<String> hashTags = new ArrayList<String>();
			while(matcher.find())
			{
				hashTags.add(matcher.group(1));
			}
			
			HashTagsContainer hashTagsContainer = (HashTagsContainer) parsedValues.get(1);
			hashTagsContainer.getHashTags().addAll(hashTags);
		}
		
		return parsedValues;
	}
	
	// Test
	public static void main(String[] args)
	{
		TweetsParser parser = new TweetsParser();
		List<Object> parsedVals = parser.parseTweets("http://t.co/sRo3OsUlsw http://t.co/FLPFGbtk4T http://t.co/ThUex0QZQK http://t.co/8VSf2o6Loi");
		
		UrlsContainer urls = (UrlsContainer) parsedVals.get(0);
		for(String url : urls.getUrls())
		{
			System.out.println(url);
		}
		
		HashTagsContainer hashTags = (HashTagsContainer) parsedVals.get(1);
		for(String tag : hashTags.getHashTags())
		{
			System.out.println("#" + tag);
		}
	}
 
}

class HashTagsContainer
{
	private List<String> tags;
	
	public HashTagsContainer()
	{
		tags = new ArrayList<String>();
	}
	
	public List<String> getHashTags()
	{
		return tags;
	}
}

class UrlsContainer
{
	private List<String> urls;
	
	public UrlsContainer()
	{
		urls = new ArrayList<String>();
	}
	
	public List<String> getUrls()
	{
		return urls;
	}
}
