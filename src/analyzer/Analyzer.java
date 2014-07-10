package analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;


//presently there is a bug where tweets that have newline characters in them really fuck up the algorithm.
//I'll try and make a sanitizer but please just remove extra newlines for now
public class Analyzer {

	public static void main(String[] args) {
		String filename = "tweets.csv";
		if(args.length > 0) {
			filename = args[0];
		}
		
		Scanner in = null;
		try {
			//for reasons I don't understand, the BufferedReader is necessary
			//otherwise it cuts out really really quickly.
			in = new Scanner(new BufferedReader(new FileReader(filename)));
			in.nextLine(); //burn the header line
		} catch (FileNotFoundException e) {
			System.out.println("tweets.csv must be in the same directory, or specified as an arg");
			e.printStackTrace();
		}
		
		//longs are used when an integer just isn't big enough
		ArrayList<Long> tweetids = new ArrayList<Long>();
		ArrayList<Long> replystatusids = new ArrayList<Long>();
		ArrayList<Long> replyuserids = new ArrayList<Long>();
		ArrayList<String> timestamps = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> tweets = new ArrayList<String>();
		ArrayList<Long> rtstatusids = new ArrayList<Long>();
		ArrayList<Long> rtuserids = new ArrayList<Long>();
		ArrayList<String> rttimestamps = new ArrayList<String>();
		ArrayList<String> expandedurls = new ArrayList<String>();
		
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split("\",\""); //this WILL BREAK if you have "," in a tweet or URL or something
			
			//if the array is not 10 entries long, the tweet text has hard returns.
			//this will attempt to chain them together -- if it ends up failing, it just won't use that line.
			while(s.length < 10) {
				String[] s2 = in.nextLine().split("\",\"");
				s[s.length-1] += " " + s2[0]; //space helps with word recognition
				s = concat(s, s2);
			}
			
			long tweetid = Long.parseLong(s[0].substring(1, s[0].length())); //cut out the first quote
			tweetids.add(tweetid);
			
			if(s[1].length() > 0) {
				replystatusids.add(Long.parseLong(s[1]));
			} else {
				replystatusids.add((long) -1); //apparently -1 is not a long, java what are you doing
			}
			
			if(s[2].length() > 0) {
				replyuserids.add(Long.parseLong(s[2]));
			} else {
				replyuserids.add((long) -1);
			}
			
			timestamps.add(s[3]);
			
			sources.add(s[4]);
			
			tweets.add(s[5]);
			
			if(s[6].length() > 0) {
				rtstatusids.add(Long.parseLong(s[6]));
			} else {
				rtstatusids.add((long) -1);
			}
			
			if(s[7].length() > 0) {
				rtuserids.add(Long.parseLong(s[7]));
			} else {
				rtuserids.add((long) -1);
			}
			
			rttimestamps.add(s[8]);
			
			String expandedurl = s[9].substring(0, s[9].length()-1); //cut out the last quote
			expandedurls.add(expandedurl);
		}
		in.close();
		System.out.println("numtweets: " + tweetids.size());
//		System.out.println("Top people whose tweets you replied to:");
//		printTopUsers(replyuserids);
//		System.out.println("Top people whose tweets you retweeted:");
//		printTopUsers(rtuserids);
		printTopTimestamps(timestamps);
	}
	
	public static <T> void printKeyValues(ArrayList<T> keys, ArrayList<Integer> values) {
		printKeyValues(keys, values, 20, false, false);
	}
	
	public static <T> void printKeyValues(ArrayList<T> keys, ArrayList<Integer> values, int num, boolean isUserIds, boolean skipTop) {
		if(num > keys.size()) num = keys.size();
		
		int current = 1;
		for(int i = keys.size()-1; i > keys.size()-1-num; i--) {
			System.out.printf("%-6d", current);
			System.out.println( keys.get(i) + ": " + values.get(i));
			current++;
		}
	}
	
	public static void printTopUsers(ArrayList<Long> input) {
		ArrayList<Long> keys = new ArrayList<Long>();
		ArrayList<Integer> values = order(input, keys);
		
		for(int i = keys.size()-2; i > keys.size()-21; i--) {
			String url = "https://twitter.com/account/redirect_by_id?id=" + keys.get(i);
			System.out.println(callURL(url) + ": " + values.get(i));
		}
	}
	
	public static void printTopTimestamps(ArrayList<String> input) {
		ArrayList<String> years = new ArrayList<String>();
		ArrayList<String> months = new ArrayList<String>();
		ArrayList<String> days = new ArrayList<String>();
		
		for(String s : input) {
			years.add(s.substring(0, 4));
			months.add(s.substring(0, 7));
			days.add(s.substring(0, 10));
		}
		
		ArrayList<String> yearkeys = new ArrayList<String>();
		ArrayList<String> monthkeys = new ArrayList<String>();
		ArrayList<String> daykeys = new ArrayList<String>();
		
		ArrayList<Integer> yearvals = order(years, yearkeys);
		ArrayList<Integer> monthvals = order(months, monthkeys);
		ArrayList<Integer> dayvals = order(days, daykeys);
		
		System.out.println("---TOP YEARS---");
		printKeyValues(yearkeys, yearvals);
		System.out.println("---TOP MONTHS---");
		printKeyValues(monthkeys, monthvals);
		System.out.println("---TOP DAYS---");
		printKeyValues(daykeys, dayvals);
	}
	
	//Given an input and output array, sort the input by number of occurrences
	//input is untouched, output is reset before computation, and its size == nums' size.
	public static <T> ArrayList<Integer> order(ArrayList<T> input, ArrayList<T> output) {
		ArrayList<Integer> nums = new ArrayList<Integer>();
		
		for(T t : input) {
			if(!output.contains(t)) {
				output.add(t);
				nums.add(1);
			} else {
				int index = output.indexOf(t);
				nums.set(index, nums.get(index) + 1);
			}
		}
		
		bubbleSort(output, nums);
		return nums;
	}
	
	public static String callURL(String myURL) {	
		URL url;
		String result = "null";
		
		try {
			url = new URL(myURL);
			URLConnection urlConn = url.openConnection();
			urlConn.connect();
			InputStream is = urlConn.getInputStream();
			result = urlConn.getURL().toString();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//this method will actually leave out the first element of b
	public static String[] concat(String[] a, String[] b) {
		String[] c = new String[a.length + b.length-1];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 1, c, a.length, b.length-1);
		return c;
	}
	
	public static <T> void bubbleSort(ArrayList<T> keys, ArrayList<Integer> values) {

		for (int k = 0; k < values.size() - 1; k++) {
			boolean isSorted = true;

			for (int i = 1; i < values.size() - k; i++) {
				if (values.get(i) < values.get(i - 1)) {
					int tempVariable = values.get(i);
					values.set(i, values.get(i - 1));
					values.set(i - 1, tempVariable);
					
					T tempVariable2 = keys.get(i);
					keys.set(i, keys.get(i - 1));
					keys.set(i - 1, tempVariable2);
					isSorted = false;
				}
			}

			if (isSorted)
				break;
		}
	}
}
