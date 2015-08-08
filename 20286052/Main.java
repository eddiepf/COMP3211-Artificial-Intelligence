import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.pow;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static int num_bidder = 0, ran_bidder = 0, num_auction = 0, bid_announced = 0, round_counter = 0;
    public static double winner_bid = 0, sec_high_bid = 0;
    public static String allowed_bid, winner_id, winner_bid_and_id;
    public static boolean bid_type = true; //true when all number in between, false when only element in the set
    public static boolean no_winner = false;
    public static ArrayList<Double> bid = new ArrayList<Double>();
    public static ArrayList<String> result = new ArrayList<String>();
    private static Scanner sc;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        initialize();
        previousRound();
        placeBid();
    }

    public static void roundsCounter() throws FileNotFoundException, IOException {
        File checkFile = new File("result.txt");

        //no previous round to check
        if (checkFile.length() == 0) {
            round_counter = 1;
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader("result.txt"));
        try {
            String line = br.readLine();

            while (true) {
                if (line.startsWith("end")) {
                    String line2 = br.readLine();
                    if ("".equals(line2) || line2 == null) {
                        String line3 = br.readLine();
                        if (line3 == null) {
                            round_counter = Integer.parseInt(line.substring(13)) + 1;
                            return;
                        }
                    }
                }
                line = br.readLine();
            }
        }
        finally {
            br.close();
        }
    }

    public static void placeBid() {
        if (bid_type) {
            if (round_counter == 1 || no_winner) {
                double bid_placed = (double) bid.get(bid.size() - 1) - 0.000000001;
                System.out.println(bid_placed);
            } else if ("20286052".equals(winner_id)) {
                //look for what the second place player bid and bid a little higher
                bidCalBetween(sec_high_bid);
            } else {
                bidCalBetween(winner_bid);
            }
        } else {
            if (round_counter == 1 || no_winner) {
                //throw in the highest bid if there is no winner or if it is the first round
                System.out.println(bid.get(bid.size() - 1));
//            } else if ("20286052".equals(winner_id)) {
//                bidCalSet(sec_high_bid);
            } else {
                //look for what the winner bid and bid one higher
                bidCalSet(winner_bid);
            }
        }
        result.clear();
        no_winner = false;
    }

    public static void bidCalSet(double cal_bid) {
        int cal_index;
        cal_index = bid.indexOf(cal_bid);

        if (cal_index == bid.size() - 1) {
            System.out.println(bid.get(bid.size() - 1));
        } else {
            System.out.println(bid.get(cal_index + 1));
        }
    }

    public static void bidCalBetween(double toBid) {
        String count_dec_places = Double.toString(Math.abs(toBid));
        int integerPlaces = count_dec_places.indexOf(".");
        int decimalPlaces = count_dec_places.length() - integerPlaces - 1;
        double placeBid = toBid + 1 / (pow(10, decimalPlaces));
        if (placeBid >= bid.get(bid.size() - 1)){
            System.out.println(bid.get(bid.size() - 1) - 0.000000001);
        }
        else{
            System.out.println(placeBid);
        }
    }

    public static void previousRound() throws FileNotFoundException, IOException {
        String line;
        //StringBuilder sb;

        roundsCounter();
        if(round_counter == 1)
            return;

        BufferedReader br = new BufferedReader(new FileReader("result.txt"));
        try {
            //sb = new StringBuilder();
            line = br.readLine();

            while (true) {
                if (line.startsWith("round") && line.startsWith(Integer.toString(round_counter - 1), 6)) {
                    break;
                }
                line = br.readLine();
                if (line == null)//check for end of file in result.txt
                {
                    return;
                }

            }
            while (!line.startsWith("end")) {
                //sb.append(line);
                //sb.append(System.lineSeparator());
                line = br.readLine();
                result.add(line);
            }

        }
        finally{
            br.close();
        }
        if (!result.isEmpty()) {
            result.remove(result.size() - 1);
        }
        //System.out.println(result.get(0));
        if (result.get(0).equals("-")) {
            //no winner
            no_winner = true;
        } 
        else {
            //winner bid
            setWinnerDetails(0);
            if (bid_announced == 0){
                sec_high_bid = winner_bid;
                return;
            }
            if(bid_type){
                if ("00".equals(winner_id)) {
                    setWinnerDetails(1);
                    if("20286052".equals(winner_id))
                        setWinnerDetails(2);
                } else if ("20286052".equals(winner_id)) {
                    String sec_highest = (String) result.get(1);
                    int comma_index = sec_highest.indexOf(",");
                    sec_high_bid = Double.parseDouble(sec_highest.substring(comma_index + 1));
                }
            }
        }
    }

    public static void setWinnerDetails(int win_index) {
        winner_bid_and_id = (String) result.get(win_index);
        int comma_index = winner_bid_and_id.indexOf(",");
        winner_id = winner_bid_and_id.substring(0, comma_index);
        winner_bid = Double.parseDouble(winner_bid_and_id.substring(comma_index + 1));
    }

    public static void initialize() throws FileNotFoundException {
        sc = new Scanner(new File("setup.txt"));
        num_bidder = Integer.parseInt(sc.nextLine());
        ran_bidder = Integer.parseInt(sc.nextLine());
        num_auction = Integer.parseInt(sc.nextLine());
        bid_announced = Integer.parseInt(sc.nextLine());
        allowed_bid = sc.nextLine();

        if (allowed_bid.contains("{")) {
            //only element in set allowed as bid
            allowed_bid = allowed_bid.replaceAll("[{}]", "");
            tokenizer(allowed_bid);
            bid_type = false;
        } else {
            //number between are allowed
            allowed_bid = allowed_bid.replaceAll("\\[", "").replaceAll("\\]", "");
            tokenizer(allowed_bid);
        }
        Collections.sort(bid);
    }

    public static void tokenizer(String temp_allowed_bid) {
        StringTokenizer st = new StringTokenizer(temp_allowed_bid, ",");
        while (st.hasMoreElements()) {
            Object temp = st.nextElement();
            bid.add(Double.parseDouble((String) temp));
        }
    }
}

