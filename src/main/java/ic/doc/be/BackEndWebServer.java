package ic.doc.be;

import ic.doc.web.ApiResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackEndWebServer {

    public BackEndWebServer(int port, DataSource dataSource) throws Exception {
        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new EndPoint(dataSource)), "/*");
        server.setHandler(handler);
        server.start();
    }

    private static class EndPoint extends HttpServlet {

        private final DataSource dataSource;

        public EndPoint(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            new ApiResponse(dataSource.data()).writeTo(resp);
        }
    }
}