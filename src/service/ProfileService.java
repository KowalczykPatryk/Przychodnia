package service;

public class ProfileService {

    private static String id = "";
    private static String firstName = "";
    private static String lastName = "";
    private static String pesel = "";
    private static String phone = "";
    private static String email = "";
    private static boolean isDoctor = false;

    private ProfileService() {}

    public static void setProfile(String id, String firstName, String lastName, String pesel, String phone, String email, boolean isDoctor) 
    {
        ProfileService.id = id;
        ProfileService.firstName = firstName;
        ProfileService.lastName = lastName;
        ProfileService.pesel = pesel;
        ProfileService.phone = phone;
        ProfileService.email = email;
        ProfileService.isDoctor = isDoctor;
    }
    public static void clearProfile() 
    {
        ProfileService.id = "";
        ProfileService.firstName = "";
        ProfileService.lastName = "";
        ProfileService.pesel = "";
        ProfileService.phone = "";
        ProfileService.email = "";
        ProfileService.isDoctor = false;
    }
    public static String getId() 
    {
        return id;
    }
    public static void setId(String id) 
    {
        ProfileService.id = id;
    }
    public static String getFirstName() 
    {
        return firstName;
    }

    public static void setFirstName(String firstName) 
    {
        ProfileService.firstName = firstName;
    }

    public static String getLastName() 
    {
        return lastName;
    }

    public static void setLastName(String lastName) {
        ProfileService.lastName = lastName;
    }
    public static String getFullName() 
    {
        return firstName + " " + lastName;
    }

    public static String getPhone() 
    {
        return phone;
    }

    public static void setPhone(String phone) {
        ProfileService.phone = phone;
    }

    public static String getEmail() 
    {
        return email;
    }

    public static void setEmail(String email) 
    {
        ProfileService.email = email;
    }
    public static String getPesel() 
    {
        return pesel;
    }
    public static void setPesel(String pesel) {
        ProfileService.pesel = pesel;
    }
    public static boolean isDoctor() 
    {
        return isDoctor;
    }
    public static void setDoctor(boolean doctor) 
    {
        isDoctor = doctor;
    }
}