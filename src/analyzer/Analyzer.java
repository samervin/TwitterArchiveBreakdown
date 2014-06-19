package analyzer;

import java.util.HashMap;
import java.util.Scanner;

public class Analyzer {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		System.out.print("How many tweets are there? [number of lines in csv file - 1] ");
		int numtweets = in.nextInt();
		
		
		
		System.out.println("Paste the 'in_reply_to_user_id' field:");
		HashMap<Integer, Integer> usersRepliedTo = new HashMap<Integer, Integer>();
		int numReplies = 0;
		
		for(int i = 0; i < numtweets; i++) {
			String s = in.nextLine();
			if(!s.equals("")) {
				int id = Integer.parseInt(s);
				numReplies++;
				
				if(usersRepliedTo.containsKey(id)) {
					usersRepliedTo.put(id, usersRepliedTo.get(id)+1);
				} else {
					usersRepliedTo.put(id, 1);
				}
			}
		}
		
		int percReplies = (int) ((double) numReplies / (double) numtweets * 100);
		System.out.println("Percentage of tweets that are replies: " + percReplies + "%");
		System.out.println(usersRepliedTo);
		
		
		in.close();
	}
}
