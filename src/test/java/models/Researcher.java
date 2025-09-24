package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Researcher extends PersonBase {

    @JsonProperty("ResearchField")
    private String researchField;

    @JsonProperty("ResearchLevel")
    private String researchLevel;   // String, pas enum

    public String getResearchField() {
        return researchField;
    }

    // Getters / Setters
    public void setResearchField(String researchField) {this.researchField = researchField;}
    public String getResearchLevel() {return researchLevel;}
    public void setResearchLevel(String researchLevel) {this.researchLevel = researchLevel;}
}
