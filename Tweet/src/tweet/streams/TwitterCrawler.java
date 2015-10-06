package tweet.streams;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TwitterCrawler {
	
	private static String dataFileDir = "D://data//streams//";
	private static String streamFilePrefix = "stream";
	private static int streamFileIndex = 0;
	private static long tweetWriteCount = 0;
	
	private static OutputStreamWriter fWriter;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat ("yyyy.MM.dd - hh:mm:ss a zzz");
	static String[] headers = {
			"tweetId",
			"authorName",
			"authorId",
			"text",
			"urls",
			"hashtags",
			"created_at"
	};

	private static StatusListener listener = new StatusListener(){
		
		private TweetsParser parser = new TweetsParser();
		
		private boolean shouldWriteToFile(Status status)
		{
//			boolean statusContainsLink = status.getText().contains("http://") || status.getText().contains("https://") || status.getText().contains("t.co")|| status.getText().contains("bit.ly") || status.getText().contains("</a>");
			
			boolean statusContainsLink = status.getText().contains("://"); 
			if (status.getLang().equalsIgnoreCase("EN") && statusContainsLink) 
			{
				String authorName = status.getUser().getName();

				// Tweet id may be polluted.
				try {
					Long.parseUnsignedLong(status.getId() + "");
				} catch (NumberFormatException nfe) {
					return false;
				}
				
				// We get authorname as 0 or null sometimes, drop those streams right away.
				boolean authorNull = authorName == null || authorName.equals("") || authorName.equals("0");
				return !authorNull;
			}
			return false;
		}

		public void onStatus(Status status) 
		{
			if(!shouldWriteToFile(status))
				return;

			List<Object> parsedList = parser.parseTweets(status.getText());
			UrlsContainer urlsContainer = (UrlsContainer) parsedList.get(0);
			
			if(urlsContainer.getUrls().isEmpty())
				return;
			
			String urls = "";
			for(String url : urlsContainer.getUrls())
			{
				urls = url + " " + urls;
			}
			
			HashTagsContainer hashTagsContainer = (HashTagsContainer) parsedList.get(1);
			String hashTags = "";
			for(String hashTag : hashTagsContainer.getHashTags())
			{
				// If hashtag contains a newline character, drop it. It is from
				// another language, anyway we would get garbled data.
				if(hashTag.contains("\n") || hashTag.contains("\r")) continue;
				
				hashTags = hashTags + "#" + hashTag;
			}
			
			// Clean the tweet text from newline characters.
			String tweetText = status.getText().replace("\n", " ");
			tweetText = tweetText.replace("\r", " ");
//			"tweetId",
//			"authorName",
//			"authorId",
//			"text",
//			"urls",
//			"hashtags",
//			"created_at"
			
			String[] row = 
				{
						status.getId() + "",
						status.getUser().getScreenName(),
						status.getUser().getId() + "",
						tweetText,
						urls,
						hashTags,
						dateFormatter.format(status.getCreatedAt()),
				};
			
			try {
				fWriter.write(String.format("\n%s\t%s\t%s\t%s\t%s\t%s\t%s", (Object[])row));
				tweetWriteCount++;
				if(tweetWriteCount == 250000)
				{
					initFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

		public void onException(Exception ex) {
			ex.printStackTrace();
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
		}
	};
	private static String[] seeds = {
			"movie", "movies", "trailor", "actor", "actress", "promotion", "acting", "imdb", "oscars", "gloden globe", "theatre", "showtime", "film festival", "sequel"
	};
	
	private static void initFile()
	{
		File streamFile = new File(dataFileDir + streamFilePrefix + (++streamFileIndex) + ".txt");
		boolean printHeader = !streamFile.exists();
		try {
			if(fWriter!=null)
				fWriter.close();
			
			fWriter = new OutputStreamWriter(new FileOutputStream(streamFile, true), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(printHeader)
			try {
				fWriter.write(String.format("\n%s\t%s\t%s\t%s\t%s\t%s\t%s", (Object[])headers));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		initFile();
		
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		
		FilterQuery query = new FilterQuery().language("EN").track(seeds);
		twitterStream.filter(query);
	}
}
