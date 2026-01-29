package service;

public class AuthService {

    public boolean login(String firstName, String lastName, String pesel, String pass, boolean isDoctor) {
        return true;
    }

    public boolean register(String firstName, String lastName, String phone, String email, String pesel, String pass,
            String confirmPass, boolean isDoctor) 
    {
        if (pass.equals(confirmPass)) {
            System.out.println("Registering as: " + (isDoctor ? "Doctor" : "Patient"));
            return true;
        }
        return false;
    }
}
