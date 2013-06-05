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

public class PostimeesCollector {

	public static void main(String[] args) throws Exception {
		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.YEAR, 2009);
		begin.set(Calendar.MONTH, 3);
		begin.set(Calendar.DATE, 1);

		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, 2009);
		end.set(Calendar.MONTH, 3);
		end.set(Calendar.DATE, 31);

		String language = "EE";
		String addr = "www";
		language = "RU";
		addr = "rus";

		OutputStream outStream = new FileOutputStream("POSTIMEES-" + language
				+ ".html");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				outStream, Charset.forName("UTF-8")));

		int total = 0;
		while (begin.before(end) || begin.equals(end)) {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat dateFormatToPrint = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat dateFormatWeekday = new SimpleDateFormat("E");

			String date = dateFormat.format(begin.getTime());
			String dateToPrint = dateFormatToPrint.format(begin.getTime());

			String url = "http://" + addr + ".postimees.ee/?d=" + date;

			System.out.println(url);

			URL postimees = new URL(url);
			URLConnection connection = postimees.openConnection();

			out.write(dateToPrint + ", "
					+ dateFormatWeekday.format(begin.getTime()));
			out.newLine();
			out.newLine();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), Charset.forName("UTF-8")));

			int count = 0;
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				inputLine = inputLine.replaceAll("</span>", "");
				inputLine = inputLine.replaceAll("<br />", "");
				inputLine = inputLine.trim();

				String title = null;

				if (!inputLine.contains("servaplokk_uudislink")
						&& (inputLine.contains("uudislink")
								|| inputLine.contains("uudise_pealkiri") || inputLine
								.contains("esil_uriba_link"))
						&& inputLine.endsWith("</a>")) {
					int aIndex = inputLine.lastIndexOf("\">") + 2;
					int aaIndex = inputLine.lastIndexOf("\" >") + 3;
					int aaaIndex = inputLine.lastIndexOf("<span>") + 6;

					aIndex = Math.max(aIndex, aaIndex);
					aIndex = Math.max(aIndex, aaaIndex);

					int bIndex = inputLine.lastIndexOf("</a>");

					if ((aIndex < 0) || (bIndex < 0)) {

					} else {
						title = inputLine.substring(aIndex, bIndex);
					}
				} else if (inputLine.contains("esileht_uudis_seotud_link")) {
					inputLine = inputLine.replaceAll("</span>", "");

					int aIndex = inputLine.lastIndexOf("<b>") + 3;
					int aaIndex = inputLine.lastIndexOf("</font>") + 7;
					aIndex = Math.min(aIndex, aaIndex);

					int bIndex = inputLine.lastIndexOf("</b></font></a>");
					int bbIndex = inputLine.lastIndexOf("</font>");
					bIndex = Math.min(bIndex, bbIndex);

					if ((aIndex < 0) || (bIndex < 0)) {

					} else {
						title = inputLine.substring(aIndex, bIndex);
					}
				}

				if (title != null) {
					count++;

					int a = inputLine.indexOf("href");
					a = inputLine.indexOf("\"", a);
					int b = inputLine.indexOf("\"", a + 1);

					out.write("<a href='" + inputLine.substring(a + 1, b)
							+ "'>" + title + "</a><br>");

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
