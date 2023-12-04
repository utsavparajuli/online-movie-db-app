import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
@WebServlet(name = "TableInfoServlet", urlPatterns = "/_dashboard/api/index")
public class TableInfoServlet extends HttpServlet {
    public static final long seralVersionUID = 1L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavemoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        request.getServletContext().log("Before Querying");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            Statement tableMetadataStatement = conn.createStatement();
            ResultSet tableMetadataResultset =
                    tableMetadataStatement.executeQuery("CALL get_table_metadata");
            JsonArray tableMetadataArray = new JsonArray();
            while(tableMetadataResultset.next()) {
                JsonObject tempJsonObject = new JsonObject();
                tempJsonObject.addProperty("table_name",
                        tableMetadataResultset.getString("TABLE_NAME"));
                tempJsonObject.addProperty("column_name",
                        tableMetadataResultset.getString("COLUMN_NAME"));
                tempJsonObject.addProperty("data_type",
                        tableMetadataResultset.getString("DATA_TYPE"));
                tableMetadataArray.add(tempJsonObject);
            }

            tableMetadataResultset.close();
            tableMetadataStatement.close();
            out.write(tableMetadataArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }

    }


}
