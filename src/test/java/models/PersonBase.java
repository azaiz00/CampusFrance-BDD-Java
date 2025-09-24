package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import enums.Gender;
import enums.Status;

public abstract class PersonBase {
    @JsonProperty("Email")
    private String email;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("Gender")
    private Gender gender;   // enum gender

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("CountryOfResidence")
    private String countryOfResidence;

    @JsonProperty("Nationality")
    private String nationality;

    @JsonProperty("PostalCode")
    private String postalCode;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Status")
    private Status status;   // enum status

    @JsonProperty("DGC")
    private int dgc;

    // Getters / Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getCountryOfResidence() { return countryOfResidence; }
    public void setCountryOfResidence(String countryOfResidence) { this.countryOfResidence = countryOfResidence; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getDgc() { return dgc; }
    public void setDgc(int dgc) { this.dgc = dgc; }
}
