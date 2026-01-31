package service;

import java.sql.*;
import java.util.*;

public class DBService {

   private Connection conn;
    private static DBService instance;

   private DBService() throws SQLException
   {
       String url = "jdbc:postgresql://" + ConnectionInfo.DBhost + ":" + ConnectionInfo.DBport + "/" + ConnectionInfo.DBname;
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
                appointment.add(rs.getString("lekarz_imie"));
                appointment.add(rs.getString("lekarz_nazwisko"));
                appointment.add(rs.getString("status"));
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
    public boolean bookAppointment(int doctorId, int patientId, String date, String startTime, String endTime) throws SQLException 
    {
        String sql = "INSERT INTO wizyta (lekarz_id, pacjent_id, data, godzina_rozpoczecia, godzina_zakonczenia, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'Umówiona')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, doctorId);
            pstmt.setInt(2, patientId);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setTime(4, Time.valueOf(startTime + ":00"));
            pstmt.setTime(5, Time.valueOf(endTime + ":00"));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    public boolean cancelAppointment(int appointmentId) throws SQLException 
    {
        String sql = "UPDATE wizyta SET status = 'Anulowana' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, appointmentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    public List<List<String>> loadPrescriptionsForPatient(int patientId) throws SQLException 
    {
        List<List<String>> prescriptions = new ArrayList<>();
        String sql = "SELECT r.id, r.numer_recepty, r.data_wystawienia, r.data_ważnosci, r.status " +
                     "FROM recepta r " +
                     "WHERE r.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> prescription = new ArrayList<>();
                prescription.add(rs.getString("id"));
                prescription.add(rs.getString("numer_recepty"));
                prescription.add(rs.getString("data_wystawienia"));
                prescription.add(rs.getString("data_ważnosci"));
                prescription.add(rs.getString("status"));
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }
    public List<List<String>> loadMedicationHistoryForPatient(int patientId) throws SQLException 
    {
        List<List<String>> medicationHistory = new ArrayList<>();
        String sql = "SELECT l.id, l.nazwa, l.substancja_czynna " +
                     "FROM lek l " +
                     "JOIN pacjent_lek pl ON l.id = pl.lek_id " +
                     "WHERE pl.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> medication = new ArrayList<>();
                medication.add(rs.getString("id"));
                medication.add(rs.getString("nazwa"));
                medication.add(rs.getString("substancja_czynna"));
                medicationHistory.add(medication);
            }
        }
        return medicationHistory;
    }
    public List<List<String>> loadDiseaseHistoryForPatient(int patientId) throws SQLException 
    {
        List<List<String>> diseaseHistory = new ArrayList<>();
        String sql = "SELECT c.id, c.nazwa, c.opis, c.czy_przewlekla, c.czy_zakazna " +
                     "FROM choroba c " +
                     "JOIN pacjent_choroba pc ON c.id = pc.choroba_id "+
                     "WHERE pc.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> disease = new ArrayList<>();
                disease.add(rs.getString("id"));
                disease.add(rs.getString("nazwa"));
                disease.add(rs.getString("opis"));
                boolean czy_przewlekla = rs.getBoolean("czy_przewlekla");
                boolean czy_zakazna = rs.getBoolean("czy_zakazna");
                if (czy_przewlekla) {
                    disease.add("Tak");
                } else {
                    disease.add("Nie");
                }
                if (czy_zakazna) {
                    disease.add("Tak");
                } else {
                    disease.add("Nie");
                }
                diseaseHistory.add(disease);
            }
        }
        return diseaseHistory;
    }
    public List<List<String>> loadAllergyHistoryForPatient(int patientId) throws SQLException 
    {
        List<List<String>> allergyHistory = new ArrayList<>();
        String sql = "SELECT a.id, a.nazwa, a.opis " +
                     "FROM alergia a " +
                     "JOIN pacjent_alergia pa ON a.id = pa.alergia_id "+
                     "WHERE pa.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> allergy = new ArrayList<>();
                allergy.add(rs.getString("id"));
                allergy.add(rs.getString("nazwa"));
                allergy.add(rs.getString("opis"));
                allergyHistory.add(allergy);
            }
        }
        return allergyHistory;
    }
    public void updatePatientData(int patientId, String firstName, String lastName, String phone, String email) throws SQLException 
    {
        String sql = "UPDATE pacjent SET imie = ?, nazwisko = ?, telefon = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setInt(5, patientId);
            pstmt.executeUpdate();
        }
    }
    public List<List<String>> loadSpecializationsForDoctor(int doctorId) throws SQLException 
    {
        String sql = "SELECT s.nazwa, ls.opis, ls.czas_trwania " +
                     "FROM lekarz_specjalizacja ls " +
                     "JOIN specjalizacja s ON ls.specjalizacja_id = s.id " +
                     "WHERE ls.lekarz_id = ?";
        List<List<String>> specializations = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> specialization = new ArrayList<>();
                specialization.add(rs.getString("nazwa"));
                specialization.add(rs.getString("opis"));
                specialization.add(rs.getString("czas_trwania"));
                specializations.add(specialization);
            }
        }
        return specializations;
    }
    public List<List<String>> loadAppointmentsForDoctor(int doctorId) throws SQLException 
    {
        List<List<String>> appointments = new ArrayList<>();
        String sql = "SELECT w.id, w.data, w.godzina_rozpoczecia, w.godzina_zakonczenia, p.id AS pacjent_id, p.imie AS pacjent_imie, p.nazwisko AS pacjent_nazwisko, p.pesel, p.telefon, w.status " +
                     "FROM wizyta w " +
                     "JOIN pacjent p ON w.pacjent_id = p.id " +
                     "WHERE w.lekarz_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> appointment = new ArrayList<>();
                appointment.add(rs.getString("id"));
                appointment.add(rs.getString("data"));
                appointment.add(rs.getString("godzina_rozpoczecia"));
                appointment.add(rs.getString("godzina_zakonczenia"));
                appointment.add(rs.getString("pacjent_id"));
                appointment.add(rs.getString("pacjent_imie"));
                appointment.add(rs.getString("pacjent_nazwisko"));
                appointment.add(rs.getString("pesel"));
                appointment.add(rs.getString("telefon"));
                appointment.add(rs.getString("status"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    public List<List<String>> loadPatientsForDoctor(int doctorId) throws SQLException 
    {
        List<List<String>> patients = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.imie, p.nazwisko, p.pesel " +
                     "FROM pacjent p " +
                     "JOIN wizyta w ON p.id = w.pacjent_id " +
                     "WHERE w.lekarz_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                List<String> patient = new ArrayList<>();
                patient.add(rs.getString("id"));
                patient.add(rs.getString("imie"));
                patient.add(rs.getString("nazwisko"));
                patient.add(rs.getString("pesel"));
                patients.add(patient);
            }
        }
        return patients;
    }
    public List<String> loadMedications() throws SQLException {
        List<String> medications = new ArrayList<>();
        String sql = "SELECT nazwa FROM lek";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                medications.add(rs.getString("nazwa"));
            }
        }
        return medications;
    }
    public List<String> loadExaminations() throws SQLException {
        List<String> examinations = new ArrayList<>();
        String sql = "SELECT nazwa FROM badanie";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                examinations.add(rs.getString("nazwa"));
            }
        }
        return examinations;
    }
    public void updateConsultationDate(int appointmentId, String date) throws SQLException {
        String sql = "UPDATE wizyta SET data = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        }
    }
    public void updateConsultationStartTime(int appointmentId, String startTime) throws SQLException {
        String sql = "UPDATE wizyta SET godzina_rozpoczecia = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setTime(1, Time.valueOf(startTime + ":00"));
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        }
    }
    public void updateConsultationEndTime(int appointmentId, String endTime) throws SQLException {
        String sql = "UPDATE wizyta SET godzina_zakonczenia = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setTime(1, Time.valueOf(endTime + ":00"));
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        }
    }
    public void savePrescription(int appointmentId, List<String> medications, String prescriptionNumber, int validityDays) throws SQLException {
        String selectSql = "SELECT pacjent_id, lekarz_id FROM wizyta WHERE id = ?";
        int patientId;
        int doctorId;
        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) 
        {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) 
            {
                patientId = rs.getInt("pacjent_id");
                doctorId = rs.getInt("lekarz_id");
            }
            else {
                throw new SQLException("Appointment not found");
            }
        }
        String insertPrescriptionSql = "INSERT INTO recepta (id, numer_recepty, pacjent_id, lekarz_id, data_wystawienia, data_ważnosci, status) " +
                                       "VALUES (?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE + INTERVAL '" + validityDays + " days', 'Wystawiona') RETURNING id";
        try (PreparedStatement pstmt1 = conn.prepareStatement(insertPrescriptionSql)) 
        {
            pstmt1.setInt(1, appointmentId);
            pstmt1.setString(2, prescriptionNumber);
            pstmt1.setInt(3, patientId);
            pstmt1.setInt(4, doctorId);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next()) 
            {
                int prescriptionId = rs.getInt("id");
                String insertMedicationSql = "INSERT INTO recepta_lek (recepta_id, lek_id) " +
                                             "VALUES (?, (SELECT id FROM lek WHERE nazwa = ?))";
                try (PreparedStatement pstmt2 = conn.prepareStatement(insertMedicationSql)) 
                {
                    for (String medication : medications) 
                    {
                        pstmt2.setInt(1, prescriptionId);
                        pstmt2.setString(2, medication);
                        pstmt2.executeUpdate();
                        String insertPatientMedicationSql = "INSERT INTO pacjent_lek (pacjent_id, lek_id) " +
                                                   "VALUES (?, (SELECT id FROM lek WHERE nazwa = ?))";
                        try (PreparedStatement pstmt4 = conn.prepareStatement(insertPatientMedicationSql)) 
                        {
                            pstmt4.setInt(1, patientId);
                            pstmt4.setString(2, medication);
                            pstmt4.executeUpdate();
                        }
                    }
                }
                String insertConsultationPrescriptionSql = "INSERT INTO wizyta_recepta (wizyta_id, recepta_id) VALUES (?, ?)";
                try (PreparedStatement pstmt3 = conn.prepareStatement(insertConsultationPrescriptionSql)) 
                {
                    pstmt3.setInt(1, appointmentId);
                    pstmt3.setInt(2, prescriptionId);
                    pstmt3.executeUpdate();
                }
            }
        }
    }

    public void saveExaminations(int appointmentId, List<String> examinations) throws SQLException {
        String insertExaminationSql = "INSERT INTO wizyta_badanie (wizyta_id, badanie_id) " +
                                      "VALUES (?, (SELECT id FROM badanie WHERE nazwa = ?))";
        try (PreparedStatement pstmt = conn.prepareStatement(insertExaminationSql)) 
        {
            for (String examination : examinations) 
            {
                pstmt.setInt(1, appointmentId);
                pstmt.setString(2, examination);
                pstmt.executeUpdate();
            }
        }
    }
    public void updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String sql = "UPDATE wizyta SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        }
    }
    public String getPatientNameByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT p.imie, p.nazwisko " +
                     "FROM wizyta w " +
                     "JOIN pacjent p ON w.pacjent_id = p.id " +
                     "WHERE w.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) 
            {
                return rs.getString("imie") + " " + rs.getString("nazwisko");
            } else {
                return "Nie znaleziono imienia pacjenta";
            }
        }
    }
    public String getPatientNameById(int patientId) throws SQLException {
        String sql = "SELECT imie, nazwisko FROM pacjent WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) 
            {
                return rs.getString("imie") + " " + rs.getString("nazwisko");
            } else {
                return "Nie znaleziono imienia pacjenta";
            }
        }
    }
    public List<String> getAllDiseases() throws SQLException {
        List<String> diseases = new ArrayList<>();
        String sql = "SELECT nazwa FROM choroba";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                diseases.add(rs.getString("nazwa"));
            }
        }
        return diseases;
    }
    public List<String> getAllAllergies() throws SQLException {
        List<String> allergies = new ArrayList<>();
        String sql = "SELECT nazwa FROM alergia";
        try (Statement stmt = conn.createStatement()) 
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) 
            {
                allergies.add(rs.getString("nazwa"));
            }
        }
        return allergies;
    }
    public List<String> getDiseasesByPatientId(int patientId) throws SQLException {
        List<String> diseases = new ArrayList<>();
        String sql = "SELECT c.nazwa " +
                     "FROM choroba c " +
                     "JOIN pacjent_choroba pc ON c.id = pc.choroba_id " +
                     "WHERE pc.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                diseases.add(rs.getString("nazwa"));
            }
        }
        return diseases;
    }
    public List<String> getAllergiesByPatientId(int patientId) throws SQLException {
        List<String> allergies = new ArrayList<>();
        String sql = "SELECT a.nazwa " +
                     "FROM alergia a " +
                     "JOIN pacjent_alergia pa ON a.id = pa.alergia_id "+
                     "WHERE pa.pacjent_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                allergies.add(rs.getString("nazwa"));
            }
        }
        return allergies;
    }
    public void addDiseaseToPatient(int patientId, String diseaseName) throws SQLException {
        String sql = "INSERT INTO pacjent_choroba (pacjent_id, choroba_id) " +
                     "VALUES (?, (SELECT id FROM choroba WHERE nazwa = ?))";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, diseaseName);
            pstmt.executeUpdate();
        }
    }
    public void removeDiseaseFromPatient(int patientId, String diseaseName) throws SQLException {
        String sql = "DELETE FROM pacjent_choroba " +
                     "WHERE pacjent_id = ? AND choroba_id = (SELECT id FROM choroba WHERE nazwa = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, diseaseName);
            pstmt.executeUpdate();
        }
    }
    public void addAllergyToPatient(int patientId, String allergyName) throws SQLException {
        String sql = "INSERT INTO pacjent_alergia (pacjent_id, alergia_id) " +
                     "VALUES (?, (SELECT id FROM alergia WHERE nazwa = ?))";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, allergyName);
            pstmt.executeUpdate();
        }
    }

    public void removeAllergyFromPatient(int patientId, String allergyName) throws SQLException {
        String sql = "DELETE FROM pacjent_alergia " +
                     "WHERE pacjent_id = ? AND alergia_id = (SELECT id FROM alergia WHERE nazwa = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, allergyName);
            pstmt.executeUpdate();
        }
    }
}