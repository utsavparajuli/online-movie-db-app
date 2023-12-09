import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.util.ArrayList;

@WebFilter(filterName = "SearchFilter", urlPatterns = "/app/api/movie-list")
public class SearchFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    FileWriter writer;
    File myfile;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try (FileWriter localWriter = new FileWriter(myfile, true)) {
            // Time measurement for TS
            long tsStartTime = System.nanoTime();

            // Execute the servlet
            chain.doFilter(request, response);

            // Calculate TS
            long tsEndTime = System.nanoTime();
            long tsElapsedTime = tsEndTime - tsStartTime;

            // Log TS value

            System.out.println("Here in searchFilter");
            localWriter.write("TS: " + tsElapsedTime + " ns\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        writer = new FileWriter(myfile, true);


//        writer.close();
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("movie-list.html");
        allowedURIs.add("movie-list.js");
        allowedURIs.add("api/movie-list");
        allowedURIs.add("style.css");
        allowedURIs.add("img/");

        String contextPath = fConfig.getServletContext().getRealPath("/");
        String xmlFilePath="/home/logs/tsMeasurement.txt";
        System.out.println(xmlFilePath);
        myfile = new File(xmlFilePath);
        try {
            myfile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        // ignored.

    }}

