import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ErrCollector {

	public static void main(String[] args) throws Exception {
		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.YEAR, 2009);
		begin.set(Calendar.MONTH, 2);
		begin.set(Calendar.DATE, 1);

		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, 2009);
		end.set(Calendar.MONTH, 2);
		end.set(Calendar.DATE, 31);

		String language = "EE";
		String addr = "uudised";
		// language = "RU";
		// addr = "novosti";

		OutputStream outStream = new FileOutputStream("ERR-" + language
				+ ".txt");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				outStream, Charset.forName("UTF-8")));

		int total = 0;
		while (begin.before(end) || begin.equals(end)) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");
			DateFormat dateFormatToPrint = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat dateFormatWeekday = new SimpleDateFormat("E");

			String date = dateFormat.format(begin.getTime());
			String dateToPrint = dateFormatToPrint.format(begin.getTime());

			URL err = new URL("http://" + addr
					+ ".err.ee/index.php?026871111111&kuu=" + date);
			URLConnection connection = err.openConnection();

			out.write(dateToPrint + ", "
					+ dateFormatWeekday.format(begin.getTime()));
			out.newLine();
			out.newLine();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), Charset.forName("UTF-8")));

			int count = 0;
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				int endIndex = inputLine.lastIndexOf("</a><br />");
				if ((endIndex > 0) && inputLine.contains(dateToPrint)) {
					out.write(inputLine.substring(29, endIndex));
					out.newLine();
					count++;
				}
			}
			out.newLine();
			out.write("Total:" + count);
			out.newLine();
			out.newLine();

			in.close();
			total += count;
			begin.add(Calendar.DATE, 1);
		}

		System.out.println(total);
		out.close();
	}
}
