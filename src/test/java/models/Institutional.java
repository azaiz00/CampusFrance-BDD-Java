package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Institutional extends PersonBase {

    @JsonProperty("Function")
    private String function;

    @JsonProperty("OrganizationType")
    private String organizationType;   // String, pas enum

    @JsonProperty("OrganizationName")
    private String organizationName;

    // Getters / Setters
    public String getFunction() {return function;}
    public void setFunction(String function) {this.function = function;}
    public String getOrganizationType() {return organizationType;}
    public void setOrganizationType(String organizationType) {this.organizationType = organizationType;}
    public String getOrganizationName() {return organizationName;}
    public void setOrganizationName(String organizationName) {this.organizationName = organizationName;}
}
