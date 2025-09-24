# language: en
Feature: Student Registration
  As a student
  I want to register successfully
  So that I can create a Campus France account

  Scenario: Valid registration in Chemistry L1
    Given I open the registration page
    And I close the cookies banner if it exists
    When I fill the form with a valid student
    Then the Student status is checked
    And I accept the conditions

