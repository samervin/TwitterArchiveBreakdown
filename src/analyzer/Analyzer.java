package analyzer;

import java.util.ArrayList;
import java.util.Scanner;

public class Analyzer {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		System.out.print("How many tweets are there? [number of lines in csv file - 1] ");
		int numtweets = in.nextInt();
		
		
		
		System.out.println("Paste the 'in_reply_to_user_id' field:");
		ArrayList<Integer> repliedids = new ArrayList<Integer>();
		ArrayList<Integer> repliednum = new ArrayList<Integer>(); //maps are dumb you can't sort them
		int numReplies = 0;
		
		for(int i = 0; i < numtweets; i++) {
			String s = in.nextLine();
			if(!s.equals("")) {
				int id = Integer.parseInt(s);
				numReplies++;
				
				if(repliedids.contains(id)) {
					int index = repliedids.indexOf(id);
					repliednum.set(index, repliednum.get(index) + 1);
				} else {
					repliedids.add(id);
					repliednum.add(1);
				}
			}
		}
		
		bubbleSort(repliedids, repliednum); //easy > good
		
		int percReplies = (int) ((double) numReplies / (double) numtweets * 100);
		System.out.println("Number of people replied to: " + repliedids.size());
		System.out.println("Number of replies: " + numReplies);
		System.out.println("Percentage of tweets that are replies: " + percReplies + "%");
		for(int i = 0; i < repliedids.size(); i++) {
			System.out.println(repliedids.get(i) + "\t\t" + repliednum.get(i));
		}
		
		
		in.close();
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
