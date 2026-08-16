
package edu.regis.shatuw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.*;


@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@Controller
class AuthController {

    // Koyeb passes these directly from the dashboard configuration
   // private final String dbUrl = System.getenv("DB_URL"); 
   // private final String dbUser = System.getenv("DB_USER");
    //private final String dbPassword = System.getenv("DB_PASSWORD");
    
    // Reads the NetBeans VM Option if present; falls back to Koyeb's Environment Variable in production
    private final String dbUrl = System.getProperty("DB_URL") != null ? 
            System.getProperty("DB_URL") : System.getenv("DB_URL"); 
            
    private final String dbUser = System.getProperty("DB_USER") != null ? 
            System.getProperty("DB_USER") : System.getenv("DB_USER");
            
    private final String dbPassword = System.getProperty("DB_PASSWORD") != null ? 
            System.getProperty("DB_PASSWORD") : System.getenv("DB_PASSWORD");

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, Model model) {
        // Enforce secure Aiven SSL connection parameter natively
        String secureUrl = dbUrl + (dbUrl.contains("?") ? "&" : "?") + "sslmode=REQUIRED";

        try (Connection conn = DriverManager.getConnection(secureUrl, dbUser, dbPassword)) {
            // Simple educational table validation
            String sql = "SELECT * FROM Account WHERE UserId = ? AND Password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    model.addAttribute("name", rs.getString("userId"));
                    return "dashboard"; // Renders dashboard.html
                }
            }
        } catch (SQLException e) {
            model.addAttribute("error", "Database Error: " + e.getMessage());
            return "login";
        }
        model.addAttribute("error", "Invalid Credentials!");
        return "login";
    }
}
