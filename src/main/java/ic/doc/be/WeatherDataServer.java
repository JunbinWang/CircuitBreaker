package ic.doc.be;

import java.util.concurrent.*;

public class WeatherDataServer extends BackEndWebServer {

    private static final int DEFAULT_PORT = 5002;

    public WeatherDataServer(int port, DataSource dataSource) throws Exception {
        super(port, dataSource);
    }

    private static class WeatherDataSource implements DataSource {

        private final ExecutorService executorService = Executors.newFixedThreadPool(1);
        private long lastRequestTime;

        @Override
        public String data() {
            return "Latest temp forecast: " + calculateTemperatureForecast() + " celcius";
        }

        // code here simulates the server getting overloaded when requests come frequently,
        // by adding an artificial delay to the processing of requests

        private int calculateTemperatureForecast() {
            Future<Integer> future = executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    busyWork();
                    return (int) (30 - (Math.random() * 30));
                }
            });

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        private void busyWork() {
            long timeSinceLastRequest = currentTime() - lastRequestTime;
            lastRequestTime = currentTime();
            long delay = Math.max(0, 15000 - timeSinceLastRequest);
            if (delay > 8000) {
                System.out.println("WARNING: Overloaded... ");
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                new RuntimeException(e);
            }
        }

        private long currentTime() {
            return System.currentTimeMillis();
        }
    }

    private static int portFrom(String[] args) {
        if (args.length < 1) {
            return DEFAULT_PORT;
        }
        return Integer.valueOf(args[0]);
    }

    public static void main(String[] args) throws Exception {
        new WeatherDataServer(portFrom(args), new WeatherDataSource());
    }
}