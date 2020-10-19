import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
// import java.util.HashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

class DateComparator implements Comparator<String> {
	DateFormat f = new SimpleDateFormat("yyyy/MM/dd");
	@Override
	public int compare(String o1, String o2) {
		try {
			return f.parse(o1).compareTo(f.parse(o2));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

public class Test{
	private static Map<String, ArrayList<String> > data_classify(String filename) {
		try {
			Map<String, ArrayList<String> > observations = new TreeMap<String, ArrayList<String> >(new DateComparator());
			File myObj = new File(filename);
			Scanner myReader = new Scanner(new FileInputStream(myObj), "utf-8");
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(line, ",", true);
				String date, location, item;
				assert tokenizer.hasMoreTokens();
				date = tokenizer.nextToken(); tokenizer.nextToken();
				assert tokenizer.hasMoreTokens();
				location = tokenizer.nextToken(); tokenizer.nextToken();
				assert tokenizer.hasMoreTokens();
				item = tokenizer.nextToken(); tokenizer.nextToken();
				ArrayList tmp = new ArrayList<String>();
				StringBuffer sb = new StringBuffer();
				if ( location.equals("大里") && item.equals("PM2.5")) {
					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						if (token.equals(",")) {
							sb.append(" ").append(",");
						} else {
							sb.append(token).append(",");
							if (tokenizer.hasMoreTokens())
								tokenizer.nextToken();
						}
					}
					tokenizer = new StringTokenizer(sb.toString(), ",");
					while (tokenizer.hasMoreTokens()) {
						String obs = tokenizer.nextToken();
						tmp.add(obs);
					}
					observations.put(date, tmp);
				}
			}
			myReader.close();
			return observations;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public static Map<String, ArrayList<Integer> > dataPreprocessing(Map<String, ArrayList<String> > observations) {
		ArrayList<Integer> older = new ArrayList<Integer>();
		ArrayList<String> odate = new ArrayList<String>();
		for (Map.Entry<String, ArrayList<String> > entry : observations.entrySet()) {
			odate.add(entry.getKey());
			// System.out.println("[" + entry.getKey() + ", " + entry.getValue() + "]");
			for (String s : entry.getValue()) {
				try {
					older.add(Integer.parseInt(s));
				} catch (NumberFormatException ex) {
					older.add(null);
				}
			}
		}
		// System.out.println(older.size());
		Iterator<Integer> iter = older.iterator();
		Integer beforei = -1;
		Integer tmp = -1;
		Integer afteri = -1;
		int beforet = -1;
		int aftert = -1;
		if(iter.hasNext()) {
			beforei = iter.next();
			beforet = 0;
		}
		while (iter.hasNext()) {
			while(iter.hasNext()) {
				tmp = iter.next();
				if (Objects.isNull(tmp)) {
					break;
				}
				beforei = tmp;
				++beforet;
			}
			// System.out.println("beforei dir : " + beforei + " beforet : " + beforet + "[ ] : " + older.get(beforet));
			aftert = beforet + 1;
			while(iter.hasNext()) {
				++aftert;
				afteri = iter.next();
				if (!Objects.isNull(afteri)) {
					// System.out.println(" beforet : " + beforet + ", aftert : " + aftert);
					// System.out.println(" [B] : " + older.get(beforet) + ", [A] : " + older.get(aftert));
					// System.out.println(" [Bi] : " + beforei + ", [Ai] : " + afteri);
					int n = aftert - beforet;
					double gap = (double)( afteri - beforei ) / n;
					for (int i = 1; i < n; ++i) {
						++beforet;
						double val = gap * i + beforei.intValue() + 0.5 ;
						// System.out.println(older.get(beforet) + " -> " +(int)val);
						older.set(beforet, (int)val);
					}
					++beforet;
					break;
				}
			}
		}
		// System.out.println(older.size());
		
		Map<String, ArrayList<Integer> > fixObservations = new TreeMap<String, ArrayList<Integer> >(new DateComparator());
		
		for (int i = 0; i < odate.size(); ++i) {
			fixObservations.put(odate.get(i), new ArrayList<Integer>(older.subList(i*24, (i+1)*24)));
		}
		older.clear();
		
		for(Map.Entry<String, ArrayList<Integer> > entry : fixObservations.entrySet()) {
			System.out.println(" " + entry.getKey() + " -> " + entry.getValue().size() + "]  " + entry.getValue());
		}
		
		return fixObservations;
	}
	
	public static void main(String[] args) {
		Map<String, ArrayList<String> > observations = data_classify(args[0]);
		Map<String, ArrayList<Integer> > fixObservations = dataPreprocessing(observations);
	}    
}
