package server;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import server.Database;
import server.User;

@SuppressWarnings("unused")
public class Main {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(9090)) {
            System.out.println("Server started at http://localhost:9090");
            while (true) {
                Socket client = server.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void handleClient(Socket client) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream()
        ) {
            String line = in.readLine();
            if (line == null) return;

            String[] parts = line.split(" ");
            if (parts.length < 2) return;

            String method = parts[0];
            String path = parts[1];

            if (method.equals("GET") && path.equals("/")) {
                sendFile(out, "public/index.html", "text/html");
            } else if (method.equals("GET") && path.equals("/report")) {
                showReport(out);
            } else if (method.equals("POST") && path.equals("/register")) {
                handlePost(in, out);
            } else if (method.equals("POST") && path.equals("/generate-pdf")) {
    try {
        server.ReportGenerator.generateUserReport();

        File pdfFile = new File("reports/user_report.pdf");
        if (!pdfFile.exists()) {
            send404(out);
            return;
        }

        byte[] content = Files.readAllBytes(pdfFile.toPath());

        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Type: application/pdf\r\n".getBytes());
        // Serve inline so browser opens PDF in tab:
        out.write(("Content-Disposition: inline; filename=\"" + pdfFile.getName() + "\"\r\n").getBytes());
        out.write(("Content-Length: " + content.length + "\r\n").getBytes());
        out.write("Connection: close\r\n".getBytes());
        out.write("\r\n".getBytes());

        out.write(content);
        out.flush();

    } catch (Exception e) {
        e.printStackTrace();
        send500(out);
    }
}
 else if (method.equals("GET") && path.startsWith("/delete")) {
                int id = Integer.parseInt(path.split("=")[1]);
                Database.deleteUser(id);
                redirect(out, "/report");
            } else if (method.equals("GET") && path.startsWith("/update")) {
                int id = Integer.parseInt(path.split("=")[1]);
                showUpdateForm(out, id);
            } else if (method.equals("POST") && path.equals("/update-user")) {
                handleUpdate(in, out);
            } else if (method.startsWith("GET") && path.endsWith(".css")) {
                sendFile(out, "public" + path, "text/css");
            } else {
                send404(out);
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendFile(OutputStream out, String filePath, String contentType) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
        send404(out);
        return;
    }

    byte[] content = Files.readAllBytes(file.toPath());
    String fileName = file.getName();

    out.write("HTTP/1.1 200 OK\r\n".getBytes());
    out.write(("Content-Type: " + contentType + "\r\n").getBytes());

    if (contentType.equals("application/pdf")) {
        out.write(("Content-Disposition: inline; filename=\"" + fileName + "\"\r\n").getBytes());
    }

    out.write(("Content-Length: " + content.length + "\r\n").getBytes());
    out.write("Connection: close\r\n".getBytes()); // Add this line
    out.write("\r\n".getBytes());
    out.write(content); //this sends the file content
    out.flush();
}


    static void send404(OutputStream out) throws IOException {
        String msg = "<h1>404 Not Found</h1>";
        out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write(("Content-Length: " + msg.length() + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(msg.getBytes());
    }

    static void send500(OutputStream out) throws IOException {
        String msg = "<h1>500 Internal Server Error</h1><p>Something went wrong.</p>";
        out.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write(("Content-Length: " + msg.length() + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(msg.getBytes());
    }

    static void handlePost(BufferedReader in, OutputStream out) throws IOException {
        int contentLength = 0;
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        char[] body = new char[contentLength];
        in.read(body);
        String postData = new String(body);
        Map<String, String> form = parseForm(postData);

        Database.saveRegistration(
            form.getOrDefault("first_name", ""),
            form.getOrDefault("middle_name", ""),
            form.getOrDefault("last_name", ""),
            form.getOrDefault("dob", ""),
            form.getOrDefault("gender", ""),
            form.getOrDefault("education", ""),
            form.getOrDefault("contact", ""),
            form.getOrDefault("address", ""),
            form.getOrDefault("username", ""),
            form.getOrDefault("password", "")
        );

        String response = "<html><head><link rel='stylesheet' href='style.css'></head><body><h1>Registration successful!</h1>"
            + "<a href='/' class='button'>Back to form</a> "
            + "<a href='/report' class='button'>View Report</a></body></html>";

        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write(("Content-Length: " + response.length() + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(response.getBytes());
    }

    static Map<String, String> parseForm(String body) {
        Map<String, String> formData = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            try {
                String key = URLDecoder.decode(kv[0], "UTF-8");
                String value = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "";
                formData.put(key, value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return formData;
    }

    static void showReport(OutputStream out) throws IOException {
    List<User> users = Database.getAllUsers();
    StringBuilder html = new StringBuilder();
    html.append("<html><head><title>Report</title><link rel='stylesheet' href='style.css'/></head><body>");
    html.append("<div class='container'><h1>Registered Users</h1>");
    html.append("<div class='report-wrapper'>"); //Start wrapper for scrollable layout

    html.append("<table class='report-table'><tr>")
        .append("<th class='button'>S.No</th><th class='button'>Name</th><th class='button'>DOB</th><th class='button'>Gender</th>")
        .append("<th class='button'>Education</th><th class='button'>Contact</th><th class='button'>Address</th><th class='button'>Username</th><th class='button'>Actions</th></tr>");

    int serial = 1;
    for (User u : users) {
        html.append("<tr>")
            .append("<td>").append(serial++).append("</td>")
            .append("<td>").append(u.firstName).append(" ").append(u.lastName).append("</td>")
            .append("<td>").append(u.dob).append("</td>")
            .append("<td>").append(u.gender).append("</td>")
            .append("<td>").append(u.education).append("</td>")
            .append("<td>").append(u.contact).append("</td>")
            .append("<td>").append(u.address).append("</td>")
            .append("<td>").append(u.username).append("</td>")
            .append("<td>")
            .append("<a href='/update?id=").append(u.id).append("' class='button'>Update</a> ")
            .append("<a href='/delete?id=").append(u.id).append("' class='button'>Delete</a>")
            .append("</td>")
            .append("</tr>");
    }

    html.append("</table></div>") //Close .report-wrapper
        .append("<form action='/generate-pdf' method='post'><button type='submit'>Download PDF</button></form><br>")
        .append("<a href='/' class='button'>Back To Form</a></div>")
        .append("</body></html>");

    byte[] content = html.toString().getBytes();
    out.write("HTTP/1.1 200 OK\r\n".getBytes());
    out.write("Content-Type: text/html\r\n".getBytes());
    out.write(("Content-Length: " + content.length + "\r\n").getBytes());
    out.write("\r\n".getBytes());
    out.write(content);
}


    static void redirect(OutputStream out, String location) throws IOException {
        out.write("HTTP/1.1 302 Found\r\n".getBytes());
        out.write(("Location: " + location + "\r\n").getBytes());
        out.write("\r\n".getBytes());
    }

    static void showUpdateForm(OutputStream out, int id) throws IOException {
        User user = Database.getUserById(id);
        if (user == null) {
            send404(out);
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Update User</title><link rel='stylesheet' href='style.css'></head><body>");
        html.append("<div class='container'><h2>Update Registration</h2><form method='POST' action='/update-user'>")
            .append("<input type='hidden' name='id' value='").append(user.id).append("'/>")
            .append("<label>First Name: <input type='text' name='first_name' value='").append(user.firstName).append("' required></label><br><br>")
            .append("<label>Middle Name: <input type='text' name='middle_name' value='").append(user.middleName).append("'></label><br><br>")
            .append("<label>Last Name: <input type='text' name='last_name' value='").append(user.lastName).append("' required></label><br><br>")
            .append("<label>Date of Birth: <input type='date' name='dob' value='").append(user.dob).append("' required></label><br><br>")
            .append("<label>Gender: <select name='gender' required>")
            .append("<option value=''>Select</option>")
            .append("<option" + ("Male".equals(user.gender) ? " selected" : "") + ">Male</option>")
            .append("<option" + ("Female".equals(user.gender) ? " selected" : "") + ">Female</option>")
            .append("<option" + ("Other".equals(user.gender) ? " selected" : "") + ">Other</option>")
            .append("</select></label><br><br>")
            .append("<label>Education: <input type='text' name='education' value='").append(user.education).append("'></label><br><br>")
            .append("<label>Contact: <input type='text' name='contact' value='").append(user.contact).append("' required></label><br><br>")
            .append("<label>Address: <textarea name='address' rows='3' required>").append(user.address).append("</textarea></label><br><br>")
            .append("<label>Username: <input type='text' name='username' value='").append(user.username).append("' required></label><br><br>")
            .append("<label>Password: <input type='password' name='password' value='").append(user.password).append("' required></label><br><br>")
            .append("<button type='submit'>Update</button>")
            .append("</form><br><a href='/report' class='button'>Back To Report</a></div>")
            .append("</body></html>");

        byte[] content = html.toString().getBytes();
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write(("Content-Length: " + content.length + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
    }

    static void handleUpdate(BufferedReader in, OutputStream out) throws IOException {
        int contentLength = 0;
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        char[] body = new char[contentLength];
        in.read(body);
        String postData = new String(body);
        Map<String, String> form = parseForm(postData);

        User u = new User(
            Integer.parseInt(form.get("id")),
            form.getOrDefault("first_name", ""),
            form.getOrDefault("middle_name", ""),
            form.getOrDefault("last_name", ""),
            form.getOrDefault("dob", ""),
            form.getOrDefault("gender", ""),
            form.getOrDefault("education", ""),
            form.getOrDefault("contact", ""),
            form.getOrDefault("address", ""),
            form.getOrDefault("username", ""),
            form.getOrDefault("password", "")
        );

        Database.updateUser(u);
        redirect(out, "/report");
    }
}
