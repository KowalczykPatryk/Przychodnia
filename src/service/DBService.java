package service;

import java.sql.*;
import java.util.*;

public class DBService {

   private Connection conn;
    private static DBService instance;

   private DBService() throws SQLException
   {
       String url = "jdbc:postgresql://localhost:55432/" + ConnectionInfo.DBname;
       conn = DriverManager.getConnection(url, ConnectionInfo.DBuser, ConnectionInfo.DBpass);
   }

   public static DBService getInstance() throws SQLException 
   {
       if (instance == null) 
        {
           instance = new DBService();
       }
       return instance;
   }

   public void executeSQLFromFile(String filePath) throws SQLException
   {
       Scanner scanner;
       try 
       {
           scanner = new Scanner(new java.io.File(filePath));
           scanner.useDelimiter(";");
           while (scanner.hasNext())
           {
               String sqlCommand = scanner.next().trim();
               if (!sqlCommand.isEmpty())
               {
                    Statement stmt = conn.createStatement();
                    stmt.execute(sqlCommand);
               }
           }
           scanner.close();
       } catch (java.io.FileNotFoundException e) 
       {
           e.printStackTrace();
       }
   }
   public boolean isDatabasePopulated() throws SQLException 
   {
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM specjalizacja");
            if (rs.next()) 
                {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public void initializeDatabase() throws SQLException 
    {
        executeSQLFromFile("src/sql/init.sql");
        
        if (!isDatabasePopulated()) 
        {
            executeSQLFromFile("src/sql/populate.sql");
        }
    }
    public boolean addDoctor(String firstName, String lastName, String phone, String email, String pesel, String pass) throws SQLException
    {
        String sql = "INSERT INTO lekarz (imie, nazwisko, telefon, email, pesel, haslo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, pesel);
            pstmt.setString(6, pass);
            pstmt.executeUpdate();
            return true;
        } 
    }
    public boolean addPatient(String firstName, String lastName, String phone, String email, String pesel, String pass) throws SQLException
    {
        String sql = "INSERT INTO pacjent (imie, nazwisko, telefon, email, pesel, haslo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, pesel);
            pstmt.setString(6, pass);
            pstmt.executeUpdate();
            return true;
        }
    }
    public boolean verifyDoctorCredentials(String firstName, String lastName, String pesel, String pass) throws SQLException 
    {
        String sql = "SELECT * FROM lekarz WHERE imie = ? AND nazwisko = ? AND pesel = ? AND haslo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, pesel);
            pstmt.setString(4, pass);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
    public boolean verifyPatientCredentials(String firstName, String lastName, String pesel, String pass) throws SQLException 
    {
        String sql = "SELECT * FROM pacjent WHERE imie = ? AND nazwisko = ? AND pesel = ? AND haslo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, pesel);
            pstmt.setString(4, pass);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
    public Map<String, String> loadProfileData(String firstName, String lastName, String pesel, boolean isDoctor) throws SQLException 
    {
        String tableName = isDoctor ? "lekarz" : "pacjent";
        String sql = "SELECT id, telefon, email FROM " + tableName + " WHERE imie = ? AND nazwisko = ? AND pesel = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, pesel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) 
            {
                Map<String, String> profileData = new HashMap<>();
                profileData.put("id", rs.getString("id"));
                profileData.put("phone", rs.getString("telefon"));
                profileData.put("email", rs.getString("email"));
                return profileData;
            } 
            else 
            {
                return Collections.emptyMap();
            }
        }
    }
    public List<String> loadSpecializations() throws SQLException 
    {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT id, nazwa FROM specjalizacja";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                specializations.add(rs.getString("nazwa"));
            }
        }
        return specializations;
    }
    public void addSpecialization(int doctorId, String name, String description, int duration) throws SQLException 
    {
        String selectSql = "SELECT id FROM specjalizacja WHERE nazwa = ?";
        try (PreparedStatement pstmt1 = conn.prepareStatement(selectSql)) 
        {
            pstmt1.setString(1, name);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next()) 
            {
                String specializationId = rs.getString("id");
                String insertSql = "INSERT INTO lekarz_specjalizacja (lekarz_id, specjalizacja_id, opis, czas_trwania) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt2 = conn.prepareStatement(insertSql);
                pstmt2.setInt(1, doctorId);
                pstmt2.setInt(2, Integer.parseInt(specializationId));
                pstmt2.setString(3, description);
                pstmt2.setInt(4, duration);
                pstmt2.executeUpdate();
            }
        }
    }
    public List<List<String>> loadAppointmentsForPatient(int patientId) throws SQLException 
    {
        List<List<String>> appointments = new ArrayList<>();
        String sql = "SELECT w.id, w.data, w.godzina_rozpoczecia, w.godzina_zakonczenia, w.status, l.imie AS lekarz_imie, l.nazwisko AS lekarz_nazwisko " +
                     "FROM wizyta w " +
                     "JOIN lekarz l ON w.lekarz_id = l.id " +
                     "JOIN pacjent p ON w.pacjent_id = p.id " +
                     "WHERE p.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> appointment = new ArrayList<>();
                appointment.add(rs.getString("id"));
                appointment.add(rs.getString("data"));
                appointment.add(rs.getString("godzina_rozpoczecia"));
                appointment.add(rs.getString("godzina_zakonczenia"));
                appointment.add(rs.getString("status"));
                appointment.add(rs.getString("lekarz_imie"));
                appointment.add(rs.getString("lekarz_nazwisko"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    public List<List<String>> loadAvailableDoctors() throws SQLException 
    {
        List<List<String>> doctors = new ArrayList<>();
        String sql = "SELECT l.id, l.imie, l.nazwisko, COALESCE(s.nazwa, 'Brak specjalizacji') AS specjalizacja " +
                     "FROM lekarz l " +
                     "LEFT JOIN lekarz_specjalizacja ls ON l.id = ls.lekarz_id " +
                     "LEFT JOIN specjalizacja s ON ls.specjalizacja_id = s.id";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                List<String> doctor = new ArrayList<>();
                doctor.add(rs.getString("id"));
                doctor.add(rs.getString("imie"));
                doctor.add(rs.getString("nazwisko"));
                doctor.add(rs.getString("specjalizacja"));
                doctors.add(doctor);
            }
        }
        return doctors;
    }
}
