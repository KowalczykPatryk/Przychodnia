package service;

import java.sql.SQLException;


public class AuthService {

    public static boolean login(String firstName, String lastName, String pesel, String pass, boolean isDoctor) 
    {
        try 
        {
            DBService dbService = DBService.getInstance();
            if (isDoctor) 
            {
                return dbService.verifyDoctorCredentials(firstName, lastName, pesel, pass);
            } 
            else 
            {
                return dbService.verifyPatientCredentials(firstName, lastName, pesel, pass);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(String firstName, String lastName, String phone, String email, String pesel, String pass,String confirmPass, boolean isDoctor) 
    {
        if (pass.equals(confirmPass) && !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !pesel.isEmpty() && !pass.isEmpty())
        {
            try 
            {
                DBService dbService = DBService.getInstance();
                if (isDoctor) 
                {
                    return dbService.addDoctor(firstName, lastName, phone, email, pesel, pass);
                } 
                else 
                {
                    return dbService.addPatient(firstName, lastName, phone, email, pesel, pass);
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
