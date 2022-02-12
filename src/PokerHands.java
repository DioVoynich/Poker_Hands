import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// A poker game, player1 vs player2, to see how many times player1 can
// win against player2. Tried my best to reduce the time complexity.
// Complicated O(n*m).
public class PokerHands {

    // input file
    public static final String file_name = "resource/p054_poker.txt";

    // score for player1
    static int score = 0;
    static Map<String, Integer> archive = new HashMap<String, Integer>();
    static Map<String, Integer> rank = new HashMap<String, Integer>();

    // ascending order
    private static void rank_constructor() {
        rank.put("HC", 1);
        rank.put("OP", 2);
        rank.put("TP", 3);
        rank.put("TK", 4);
        rank.put("S", 5);
        rank.put("F", 6);
        rank.put("FH", 7);
        rank.put("FK", 8);
        rank.put("SF", 9);
        rank.put("RF", 10);
    }

    // store the actual value of each card
    private static void archive_constructor() {
        archive.put("2", 1);
        archive.put("3", 2);
        archive.put("4", 3);
        archive.put("5", 4);
        archive.put("6", 5);
        archive.put("7", 6);
        archive.put("8", 7);
        archive.put("9", 8);
        archive.put("T", 9);
        archive.put("J", 10);
        archive.put("Q", 11);
        archive.put("K", 12);
        archive.put("A", 13);
    }

    // check does player1 wins or not
    public static void battle(String[] p1, String[] p2) {
        int rank1 = rank.get(p1[0]);
        int rank2 = rank.get(p2[0]);

        if (rank1 > rank2) {        // primary
            score++;
        } else if (rank1 == rank2) {
            int secondary1 = Integer.parseInt(p1[1]);
            int secondary2 = Integer.parseInt(p2[1]);
            if (secondary1 > secondary2) {      // secondary
                score++;
            } else if (secondary1 == secondary2) {      // tertiary
                int tertiary1 = Integer.parseInt(p1[2]);
                int tertiary2 = Integer.parseInt(p2[2]);
                if (tertiary1 > tertiary2) {
                    score++;
                }
            }
        }
    }

    // check the consecutive order
    private static boolean consecutive_checker(int[] storage) {
        for (int i = 0; i < storage.length - 1; i++) {
            if (storage[i] + 1 != storage[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // find the second-largest element
    private static int getRemain(Map<String, Integer> numbers, String omit) {
        int max = 0;
        for (String key : numbers.keySet()) {
            if  (!key.equals(omit)) {
                max = Math.max(max, archive.get(key));
            }
        }
        return max;
    }


    // deciding the score
    public static String poker_helper(String[] player) {
        String outcome = "";

        // sorting them into numbers and suits
        Map<String, Integer> numbers = new HashMap<String, Integer>();
        Set<String> suits = new HashSet<String>();
        int largest = 0;    // storing the max value
        int sum = 0;

        // sort them out by counting them
        for (String current : player) {
            String icon = current.substring(current.length() - 1);
            String num = current.substring(0, current.length() - 1);

            numbers.put(num, numbers.getOrDefault(num, 0) + 1);
            suits.add(icon);
        }

        // different cases

        // High Card, Straight, Flash, Straight Flash, or Royal Flash
        if (numbers.size() == 5) {
            int[] storage = new int[5];
            int index = 0;
            for (String key : numbers.keySet()) {
                storage[index] = archive.get(key);
                sum += archive.get(key);
                index++;
            }
            Arrays.sort(storage);
            boolean trigger = consecutive_checker(storage);

            if (trigger) {
                if (suits.size() == 1) {    // Royal Flash or Straight Flash
                    outcome = (storage[4] == 13) ? "RF-13-0" : "SF- " + storage[4] + "-0";
                } else {
                    outcome = "S-" + storage[4] + "-0";     // Straight
                }
            } else {
                // Flush or High Card
                outcome = (suits.size() == 1) ? "F-" + storage[4] + "-" + sum : "HC-" + storage[4] + "-" + sum;

            }
        } else if (numbers.size() == 4) {   // One Pair
            for (String key : numbers.keySet()) {
                if (numbers.get(key) == 2) {
                    outcome = "OP-" + archive.get(key);

                } else {
                    largest = Math.max(largest, archive.get(key));
                }
            }
            outcome += "-" + largest;

        } else if (numbers.size() == 3) {
            String reminder = "";
            for (String key : numbers.keySet()) {
                if (numbers.get(key) == 3) {    // Three of a kind
                    outcome = "TK-" + archive.get(key) + "-" + getRemain(numbers, key);
                    return outcome;
                }

                if (numbers.get(key) == 2) {    // Two Pairs

                    if (largest < archive.get(key) ) {
                        largest = archive.get(key);
                        reminder = key;
                    }
                }
            }
            outcome = "TP-" + largest + "-" + getRemain(numbers, reminder);

        } else if (numbers.size() == 2) {
            int first = 0, second = 0;

            for (String key : numbers.keySet()) {

                if (numbers.get(key) == 4) {    // Four of a Kind
                    outcome = "FK-" + archive.get(key) + "-" + getRemain(numbers, key);
                    return outcome;
                }

                if (numbers.get(key) == 3) {
                    first = archive.get(key);
                } else {
                    second = archive.get(key);
                }
            }

            // Full House
            outcome = "FH-" + first + "-" + second;
        }

        return outcome;
    }

    public static void main(String[] args) throws IOException {

        // create the archive
        archive_constructor();

        // create ranking system
        rank_constructor();

        // reading
        BufferedReader reader = new BufferedReader(new FileReader(file_name));

        String current;
        while ((current = reader.readLine()) != null) {
            String[] player1 = current.substring(0, 14).split(" ");
            String[] player2 = current.substring(15).split(" ");

            String result1 = poker_helper(player1);
            String result2 = poker_helper(player2);

            // convert to array
            String[] data1 = result1.split("-");
            String[] data2 = result2.split("-");

            // check if player 1 is superior to player 2
            battle(data1, data2);
        }

        // final result
        System.out.println(score);
    }
}
