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
import java.util.stream.Stream;
import java.util.stream.IntStream;

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
			Map<String, ArrayList<String> > classifier = new TreeMap<String, ArrayList<String> >(new DateComparator());
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
					classifier.put(date, tmp);
				}
			}
			myReader.close();
			return classifier;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return null;
		}
	}
	
	public static Map<String, ArrayList<Integer> > dataPreprocessing(Map<String, ArrayList<String> > observations) {
		ArrayList<Integer> older = new ArrayList<Integer>();
		ArrayList<String> odate = new ArrayList<String>();
		for (Map.Entry<String, ArrayList<String> > entry : observations.entrySet()) {
			odate.add(entry.getKey());
			for (String s : entry.getValue()) {
				try {
					older.add(Integer.parseInt(s));
				} catch (NumberFormatException ex) {
					older.add(null);
				}
			}
		}
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
			aftert = beforet + 1;
			while(iter.hasNext()) {
				++aftert;
				afteri = iter.next();
				if (!Objects.isNull(afteri)) {
					int n = aftert - beforet;
					double gap = (double)( afteri - beforei ) / n;
					for (int i = 1; i < n; ++i) {
						++beforet;
						double val = gap * i + beforei.intValue() + 0.5 ;
						older.set(beforet, (int)val);
					}
					++beforet;
					break;
				}
			}
		}
		
		Map<String, ArrayList<Integer> > dataResult = new TreeMap<String, ArrayList<Integer> >(new DateComparator());
		
		for (int i = 0; i < odate.size(); ++i) {
			dataResult.put(odate.get(i), new ArrayList<Integer>(older.subList(i*24, (i+1)*24)));
		}
		
		// for(Map.Entry<String, ArrayList<Integer> > entry : dataResult.entrySet()) {
			// System.out.println(" " + entry.getKey() + " -> " + entry.getValue().size() + "]  " + entry.getValue());
		// }
		
		return dataResult;
	}
	
	public static void main(String[] args) {
		Map<String, ArrayList<String> > classifier = data_classify(args[0]);
		Map<String, ArrayList<Integer> > observations = dataPreprocessing(classifier);
		
		ArrayList<String> keysArr = new ArrayList<String>(observations.keySet());
		Set<String> centers = new TreeSet<String>();

		int[] stream = IntStream.generate(()-> (new Random()).nextInt(observations.size()))
			.distinct()
            .limit(4)
			.toArray();
        for(int i : stream) {
			centers.add(keysArr.get(i));
		}
		for(String center : centers) {
			System.out.println(center);
		}
	}    
}
