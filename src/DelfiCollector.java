import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DelfiCollector {

	private static String getTime(String link) throws IOException {
		URL url = new URL(link);
		URLConnection connection = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection
				.getInputStream(), Charset.forName("UTF-8")));

		String inputLine;
		boolean head = false;
		while ((inputLine = in.readLine()) != null) {
			inputLine = inputLine.trim();
			int i = inputLine.lastIndexOf(":");
			if (head && (i > 0)) {
				return inputLine.substring(i - 2, i + 3);
			} else {
				if (inputLine.contains("articleHead")) {
					head = true;
				}
			}
		}

		return null;
	}

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
		String addr = "www";
		language = "RU";
		addr = "rus";

		OutputStream outStream = new FileOutputStream("DELFI-" + language
				+ ".html");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				outStream, Charset.forName("UTF-8")));

		int total = 0;

		while (begin.before(end) || begin.equals(end)) {
			DateFormat dateFormatToPrint = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat dateFormatWeekday = new SimpleDateFormat("E");

			String dateToPrint = dateFormatToPrint.format(begin.getTime());

			URL postimeesEE = new URL("http://" + addr
					+ ".delfi.ee/archive/index.php?ndate="
					+ (begin.getTime().getTime() / 1000));

			URLConnection connection = postimeesEE.openConnection();

			out.write("<br><br>" + dateToPrint + ", "
					+ dateFormatWeekday.format(begin.getTime()) + "<br><br>");
			out.newLine();
			out.newLine();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), Charset.forName("UTF-8")));

			int count = 0;
			int step = 0;

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				inputLine = inputLine.trim();
				String title = null;
				String link = null;
				String time = null;
				String fullLine = null;

				if (inputLine.contains("Новости дня")) {
					step++;
					if (step == 5) {
						break;
					}
				}

				if (inputLine.contains("arArticleT")) {
					int aIndex = inputLine.indexOf("\">") + 2;
					int bIndex = inputLine.lastIndexOf("<");

					if ((aIndex < 0) || (bIndex < 0)) {
						continue;
					}

					title = inputLine.substring(aIndex, bIndex);

					bIndex = aIndex - 2;
					aIndex = inputLine.indexOf("href") + 6;

					if ((aIndex < 0) || (bIndex < 0)) {
						continue;
					}

					link = inputLine.substring(aIndex, bIndex);

					time = getTime(link);
				}

				if ((title != null) && (link != null)) {
					count++;
					fullLine = (time == null ? "" : time + " ") + "<a href='"
							+ link + "'>" + title + "</a><br>";

					// System.out.println(fullLine);
					out.write(fullLine);
					out.newLine();
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
