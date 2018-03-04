package ic.doc.be;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewsDataServer extends BackEndWebServer {

    private static final int DEFAULT_PORT = 5001;

    public NewsDataServer(int port, DataSource dataSource) throws Exception {
        super(port, dataSource);
    }

    private static class NewsDataSource implements DataSource {

        private final List<String> headlines = Arrays.asList(
                "Cost of Rail Fares Increases by 1.1%",
                "Government Minister Suspected of Scandal",
                "Better Gravity Measure Boosts Search for Life",
                "Wenger Looks to Bring Back Former Arsenal Midfielder",
                "Profits Up For Online Retailers"
        );

        @Override
        public String data() {
            Collections.shuffle(headlines);
            return headlines.get(0);
        }
    }

    private static int portFrom(String[] args) {
        if (args.length < 1) {
            return DEFAULT_PORT;
        }
        return Integer.valueOf(args[0]);
    }

    public static void main(String[] args) throws Exception {
        new NewsDataServer(portFrom(args), new NewsDataSource());
    }
}
