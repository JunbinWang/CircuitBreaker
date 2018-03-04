package ic.doc.web;

import java.io.PrintWriter;

public class IndexPage extends HtmlPage {

    private final String newsData;
    private final String weatherData;

    public IndexPage(String newsData, String weatherData) {
        this.newsData = newsData;
        this.weatherData = weatherData;
    }

    @Override
    protected void writeContentTo(PrintWriter writer) {
        writer.println("<h1> Daily Planet</h1>" + newsData() + weatherData());
    }

    private String weatherData() {
        if (null == weatherData || weatherData.isEmpty()) {
            return "";
        }
        return "<h2>Weather Forecast:</h2>" + "<p>" + weatherData + "</p>";
    }

    private String newsData() {
        if (null == newsData || newsData.isEmpty()) {
            return "";
        }
        return "<h2>News Headlines:</h2>" + "<p>" + newsData + "</p>";
    }

}
