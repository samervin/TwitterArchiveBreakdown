package analyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		printTop(replyuserids);
	}
	
	public static void printTop(ArrayList<Long> input) {
		ArrayList<Long> keys = new ArrayList<Long>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		for(Long l : input) {
			if(!keys.contains(l)) {
				keys.add(l);
				values.add(1);
			} else {
				int index = keys.indexOf(l);
				values.set(index, values.get(index) + 1);
			}
		}
		
		bubbleSort(keys, values);
		for(int i = 0; i < keys.size(); i++) {
			System.out.println(keys.get(i) + ": " + values.get(i));
		}
	}
	
	public static void bubbleSort(ArrayList<Long> keys, ArrayList<Integer> values) {

		for (int k = 0; k < values.size() - 1; k++) {
			boolean isSorted = true;

			for (int i = 1; i < values.size() - k; i++) {
				if (values.get(i) < values.get(i - 1)) {
					int tempVariable = values.get(i);
					values.set(i, values.get(i - 1));
					values.set(i - 1, tempVariable);
					
					long tempVariable2 = keys.get(i);
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
