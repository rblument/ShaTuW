
package edu.regis.shatuw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.*;

// -DDB_URL=jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/<DB_NAME> -DDB_USER=your_user -DDB_PASSWORD=your_password

// Rick local
//-DDB_URL=jdbc:mysql://localhost:3306/ShaTuDB -DDB_USER=ShaTuTs -DDB_PASSWORD=ShaTu2023
// Test local  http://localhost:8080

//-DDB_URL=jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/<DB_NAME> -DDB_USER=your_user -DDB_PASSWORD=your_password
// mysql://avnadmin:AVNS_0jf1wgXtMaoLnhkNotI@mysql-shatuw1-shatuw1.c.aivencloud.com:22099/defaultdb?ssl-mode=REQUIRED
// Database Name: defaultdb
// Host: mysql-shatuw1-shatuw1.c.aivencloud.com
// Port: 22099
// User: avnadmin
// Password: AVNS_0jf1wgXtMaoLnhkNotI
// SSL Mode: REQUIRED
// Certificate: 
/*
-----BEGIN CERTIFICATE-----
MIIERDCCAqygAwIBAgIUbxm64q9nK2dtStdE9TNfFbm2MVcwDQYJKoZIhvcNAQEM
BQAwOjE4MDYGA1UEAwwvNDliMGIxMGItYTYyZS00NjgzLTk2ZDItZmEyNDNhMmRh
YzQ5IFByb2plY3QgQ0EwHhcNMjYwODEzMTYzOTQ5WhcNMzYwODEwMTYzOTQ5WjA6
MTgwNgYDVQQDDC80OWIwYjEwYi1hNjJlLTQ2ODMtOTZkMi1mYTI0M2EyZGFjNDkg
UHJvamVjdCBDQTCCAaIwDQYJKoZIhvcNAQEBBQADggGPADCCAYoCggGBALWSVPlo
03Jd2rBYbS2ASrQk/aRQItdDzN7Lf0m6Z7YhysTc5M5zSjYSCWgZrXrjB9PaILt9
e1OUQgDdCH2eRxGj5LhQw68/S3t/kpjHWZWyeYZR2yBEY5vwaGiTFs2uU7r/ri8L
ougWNEwewslyvAUcinEco0VR0Ay3oejfkvZXAiHj1/aVj2eAWbQVfFk8P01oeK+v
ftRaMvaSFwQZwv/UaotQxBQMUGQ/DwJt5foPJQT868ovIv+cD8U1tvgVU77MwzjQ
qAjEO1wvqiCqYFQ4K5KmwZANTWj/psxd2NGEu2RIkF8qA8YQB+9nn0ICJ8+sPCAf
hJc2fMZT5znjkx6rvwmrWjK/8NW4qKZG25X3BmkMAMVurpTWVI4WmyGKB2HtkfAc
LEUPjlTZljP/mMjigHsqU71C7Se5XBumUySwQoz6rCk/jI0Itu58nu5ZXZl/IlEX
ZtjcD7rCy64ITqAWqkYVUIF47pVurlAu+W3G96U7ompSMKxBBPVaKbpSFQIDAQAB
o0IwQDAdBgNVHQ4EFgQUI3b589DLdkJjB0+fnJFEDGh7gBEwEgYDVR0TAQH/BAgw
BgEB/wIBADALBgNVHQ8EBAMCAQYwDQYJKoZIhvcNAQEMBQADggGBAFivisUl2UOp
35Fvzx/MOkabrE67y8PuAIMw5oRqJqWG2+nG1rF8yfFMt8uQUrLZTDaLhX7upj2h
6wQo9VhkOzEpx7EqTNc5DR47/6nOaCN56f5Yp48TYAyiYIaWwhZGQZXxmUj5TE0c
EQfgmVcyrgIC+qCGCr6IwhQud1tYvPZ9U3PXYvOmy1Q9LyvZFVjn4eCFrIBCVf8P
n/zk0Q6Z5DrIxmOqwSpqeP2zNMQY/yvvSkKdjJbCvgLv5OlgXm0IL0VfvQGL+LfF
zMlwPNNv8BMFJ4o3C5pzEYOiFBJfKpkmXZHSovfZzy+yfauhE4cW8D32eJNlsQO9
QuqfReX+yofrW/nTVaF2aO95oK+AVlDwWCo/A1nrtwfkCDA+WtK7qPoPjFwScBk5
hV29hugYlFVCfOSLMWumBGQC/zYNCWDWAEaCR7KAlGTugUgEl0BWPqdAUqAfMrrJ
sw/7jte3I2tcgIz7WE4Lp9oGq2BACIuPe+cPA51Lr1lTsS2fltqmPg==
-----END CERTIFICATE-----
*/



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
