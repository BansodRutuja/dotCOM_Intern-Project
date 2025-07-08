package server;

public class User {
    public int id;
    public String firstName, middleName, lastName, dob, gender, education, contact, address, username, password;

    public User(int id, String fn, String mn, String ln, String dob, String gender, String edu, String contact, String addr, String user, String pass) {
        this.id = id;
        this.firstName = fn;
        this.middleName = mn;
        this.lastName = ln;
        this.dob = dob;
        this.gender = gender;
        this.education = edu;
        this.contact = contact;
        this.address = addr;
        this.username = user;
        this.password = pass;
    }
}
