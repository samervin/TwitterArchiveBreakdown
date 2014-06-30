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
			in.nextLine();
		} catch (FileNotFoundException e) {
			System.out.println("tweets.csv must be in the same directory, or specified as an arg");
			e.printStackTrace();
		}
		
		int numtweets = 0;
		ArrayList<Long> tweetids = new ArrayList<Long>(); //longs are currently big enough by 1 order
		ArrayList<Long> replystatusids = new ArrayList<Long>(); //integers are just 1 order too small
		ArrayList<Integer> replyuserids = new ArrayList<Integer>();
		ArrayList<String> timestamps = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> tweets = new ArrayList<String>();
		ArrayList<Integer> rtstatusids = new ArrayList<Integer>();
		ArrayList<Integer> rtuserids = new ArrayList<Integer>();
		ArrayList<String> rttimestamps = new ArrayList<String>();
		ArrayList<String> expandedurls = new ArrayList<String>();
		
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split(",");
			
			long tweetid = Long.parseLong(s[0].substring(1, s[0].length()-1)); //cut out the quotes
			tweetids.add(tweetid);
			
			String replystatusid = s[1].substring(1, s[1].length()-1); //cut out the quotes
			if(replystatusid.length() > 0) {
				replystatusids.add(Long.parseLong(replystatusid));
			} else {
				replystatusids.add((long) -1); //apparently -1 is not a long, java what are you doing
			}
		}	
		
		in.close();
		System.out.println("numtweets: " + tweetids.size());
	}
	
	public static void bubbleSort(ArrayList<Integer> ids, ArrayList<Integer> num) {

		for (int k = 0; k < num.size() - 1; k++) {
			boolean isSorted = true;

			for (int i = 1; i < num.size() - k; i++) {
				if (num.get(i) < num.get(i - 1)) {
					int tempVariable = num.get(i);
					num.set(i, num.get(i - 1));
					num.set(i - 1, tempVariable);
					
					tempVariable = ids.get(i);
					ids.set(i, ids.get(i - 1));
					ids.set(i - 1, tempVariable);
					isSorted = false;
				}
			}

			if (isSorted)
				break;
		}
	}
}
