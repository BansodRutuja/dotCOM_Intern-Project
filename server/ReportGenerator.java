// Source code is decompiled from a .class file using FernFlower decompiler.
package server;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportGenerator {
   private static final String DB_URL = "jdbc:mysql://localhost:3306/registration_db";
   private static final String DB_USER = "myuser";
   private static final String DB_PASS = "mypassword";

   public ReportGenerator() {
   }

   public static void generateUserReport() {
      Document var0 = new Document();

      try {
         PdfWriter.getInstance(var0, new FileOutputStream("reports/user_report.pdf"));
         var0.open();
         Paragraph var1 = new Paragraph("User Registration Report", FontFactory.getFont("Helvetica", 18.0F, 1));
         var1.setAlignment(1);
         var0.add(var1);
         var0.add(new Paragraph(" "));
         PdfPTable var2 = new PdfPTable(5);
         var2.setWidthPercentage(100.0F);
         var2.addCell("ID");
         var2.addCell("First Name");
         var2.addCell("Last Name");
         var2.addCell("Username");
         var2.addCell("Contact");
         Class.forName("com.mysql.cj.jdbc.Driver");
         Connection var3 = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "myuser", "mypassword");
         Statement var4 = var3.createStatement();
         ResultSet var5 = var4.executeQuery("SELECT id, first_name, last_name, username, contact FROM registration");

         while(var5.next()) {
            var2.addCell(String.valueOf(var5.getInt("id")));
            var2.addCell(var5.getString("first_name"));
            var2.addCell(var5.getString("last_name"));
            var2.addCell(var5.getString("username"));
            var2.addCell(var5.getString("contact"));
         }

         var0.add(var2);
         var0.close();
         var3.close();
         System.out.println("✅ Report generated successfully.");
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static void main(String[] var0) {
      generateUserReport();
   }
}
