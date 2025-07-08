package server;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/registration_db";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC driver loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
        }
    }

    public static void saveRegistration(String fn, String mn, String ln, String dob, String gender, String edu, String contact, String addr, String user, String pass) {
        String query = "INSERT INTO registration (first_name, middle_name, last_name, dob, gender, education, contact, address, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fn);
            stmt.setString(2, mn);
            stmt.setString(3, ln);
            stmt.setString(4, dob);
            stmt.setString(5, gender);
            stmt.setString(6, edu);
            stmt.setString(7, contact);
            stmt.setString(8, addr);
            stmt.setString(9, user);
            stmt.setString(10, pass);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM registration ORDER BY id ASC")) {  // ORDER BY id ascending

        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                rs.getString("dob"),
                rs.getString("gender"),
                rs.getString("education"),
                rs.getString("contact"),
                rs.getString("address"),
                rs.getString("username"),
                rs.getString("password")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error fetching users: " + e.getMessage());
    }
    return users;
}


    public static void deleteUser(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM registration WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }
    }

    public static User getUserById(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM registration WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("education"),
                    rs.getString("contact"),
                    rs.getString("address"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    // Revised updateUser method to update all fields using User object
    public static void updateUser(User user) {
        String sql = "UPDATE registration SET first_name = ?, middle_name = ?, last_name = ?, dob = ?, gender = ?, education = ?, contact = ?, address = ?, username = ?, password = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.firstName);
            stmt.setString(2, user.middleName);
            stmt.setString(3, user.lastName);
            stmt.setString(4, user.dob);
            stmt.setString(5, user.gender);
            stmt.setString(6, user.education);
            stmt.setString(7, user.contact);
            stmt.setString(8, user.address);
            stmt.setString(9, user.username);
            stmt.setString(10, user.password);
            stmt.setInt(11, user.id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public static void updateUser(int id, String firstName, String lastName, String username) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }
}
