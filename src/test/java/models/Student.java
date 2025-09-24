package models;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Student extends PersonBase {
    @JsonProperty("StudyField")
    private String studyField;

    @JsonProperty("StudyLevel")
    private String studyLevel;   // String, pas enum

    // Getters / Setters
    public String getStudyField() { return studyField; }
    public void setStudyField(String studyField) { this.studyField = studyField; }
    public String getStudyLevel() { return studyLevel; }
    public void setStudyLevel(String studyLevel) { this.studyLevel = studyLevel; }
}
