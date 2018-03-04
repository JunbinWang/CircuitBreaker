package ic.doc.fe;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.*;

public class CircuitBreaker {

    private static final int CLOSED_STATUS = 1;
    private static final int OPEN_STATUS = 2;
    private static final int HALF_OPEN_STATUS = 3;

    private static final int  CLOSED_TIME_OUT = 5000;
    private static final int  ALLOW_FAILURE_TIMES = 3;
    private static final int  RETRY_DURATION = 15000;
    private static final int  ALLOW_RETRY_TIMES = 3;

    int status = CLOSED_STATUS;
    String uri = "";
    long last_failure_time = 0;
    int failure_counter = 0;
    int retry_times = 0;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();



    public CircuitBreaker(String uri){
        this.uri = uri;
    }

    private String fetchDataFrom() {
        try {
            return Request.Get(uri).execute().returnContent().asString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchDataWithTimeOut() throws TimeoutException {
        String result = "";
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return fetchDataFrom();
            }
        });
        executorService.execute(futureTask);
        try {
            result = futureTask.get(CLOSED_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new TimeoutException();
        }
        return result;
    }

    public String retrieveData() {
        String result = "Server is busy, please try later! ";
        switch (status){
            case OPEN_STATUS:
                long time_passed = System.currentTimeMillis() - last_failure_time;
                if(time_passed > RETRY_DURATION)
                    status = HALF_OPEN_STATUS;
                break;
            case HALF_OPEN_STATUS:
                try {
                  result = fetchDataWithTimeOut();
                } catch (TimeoutException e) {
                    last_failure_time = System.currentTimeMillis();  // reset last_failure_time
                    status = OPEN_STATUS;
                    return result;
                }
                retry_times++;
                if(retry_times > ALLOW_RETRY_TIMES){
                    status = CLOSED_STATUS;
                    retry_times = 0;  //reset retry_times;
                }
                break;
            case CLOSED_STATUS:
                try {
                    result = fetchDataWithTimeOut();
                } catch (TimeoutException e) {
                    failure_counter++;
                    if (failure_counter > ALLOW_FAILURE_TIMES) {
                        status = OPEN_STATUS; // change status to open
                        failure_counter = 0;  //reset failure counter
                        last_failure_time = System.currentTimeMillis();  // reset last_failure_time
                    }
                }
                break;
            default:
                break;

        }
        return result;
    }
}
