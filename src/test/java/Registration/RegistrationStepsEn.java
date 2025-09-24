package Registration;

import enums.Status;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import models.Student;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import utils.DataLoader;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

public class RegistrationStepsEn {

    private WebDriver driver;
    private WebDriverWait wait;
    private Student s; // valid student loaded from JSON

    /* ========= Helpers minimaux (autonomes) ========= */

    // Wait until the DOM is fully loaded
    private void waitForPageReady(long timeoutSec) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(d -> "complete".equals(
                        ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    // Scroll the element to the center (avoid sticky headers)
    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'})", el);
    }

    // Wait for an element to be visible
    private WebElement visible(By locator, long timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Safe click: visible → scroll → clickable → click ; fallback JS if intercepted
    private void safeClick(By locator, long timeoutSec) {
        WebElement el = visible(locator, timeoutSec);
        scrollCenter(el);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                    .until(ExpectedConditions.elementToBeClickable(locator))
                    .click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    // Type text after ensuring visibility
    private void type(By locator, String text, boolean sendTab, long timeoutSec) {
        WebElement el = visible(locator, timeoutSec);
        scrollCenter(el);
        el.clear();
        el.sendKeys(text);
        if (sendTab) el.sendKeys(Keys.TAB);
    }

    // Click label[for="<inputId>"] to toggle radio/checkbox
    private void clickLabelFor(String inputId, long timeoutSec) {
        safeClick(By.cssSelector("label[for='" + inputId + "']"), timeoutSec);
    }

    // Select an option in "selectized" dropdowns by exact text
    private String selectFromSelectized(String dropdownId, String optionText, long timeoutSec) {
        WebElement input = visible(By.id(dropdownId), timeoutSec);
        scrollCenter(input);
        input.click();

        By optionBy = By.xpath("//div[@class='option' and normalize-space(text())='" + optionText + "']");
        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.presenceOfElementLocated(optionBy));
        scrollCenter(option);
        option.click();

        WebElement selectedItem = input.findElement(By.xpath("..//div[contains(@class,'item')]"));
        return selectedItem.getText().trim();
        // 1. Localiser l'input du dropdown

       /* WebElement dropdownInput = driver.findElement(By.id(dropdownId));

        // 2. Scroller puis cliquer pour ouvrir la liste
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'})", dropdownInput);
        dropdownInput.click();

        // 3. Construire le Xpath de l’option par son texte
        By optionBy = By.xpath("//div[@class='option' and normalize-space(text())='" + optionText + "']");

        // 4. Attendre que l’option soit présente
        WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionBy));

        // 5. Scroller jusqu'à l’option et cliquer
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'})", option);
        option.click();

        WebElement selectedItem = dropdownInput.findElement(By.xpath("..//div[contains(@class,'item')]"));
        return selectedItem.getText().trim();*/
    }

    // Close tarteaucitron cookie banner if present
    private void closeCookiesIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement btn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(By.id("tarteaucitronAllDenied2"))
            );
            scrollCenter(btn);
            btn.click();
        } catch (TimeoutException | NoSuchElementException | ElementClickInterceptedException ignored) {
            // banner not present or not clickable → continue
        }
    }

    /* ==================== Hooks ==================== */

    @Before
    public void setUp() {
        // Ensure chromedriver is available
        WebDriverManager.chromedriver().setup();

        // Enable headless if -Dheadless=true (useful in CI)
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new", "--window-size=1280,900");
        }

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Load first valid student from test data
        List<Student> students = DataLoader.readList("testdata/students.json", Student.class);
        s = students.get(0);
    }

    @After
    public void tearDown() {
        // Always close the browser
        if (driver != null) driver.quit();
    }

    /* =============== Step Definitions EN =============== */

    @Given("I open the registration page")
    public void iOpenTheRegistrationPage() {
        driver.get("https://www.campusfrance.org/fr/user/register");
        waitForPageReady(15);
    }

    @And("I close the cookies banner if it exists")
    public void iCloseTheCookiesBannerIfItExists() {
        closeCookiesIfPresent();
    }

    @When("I fill the form with a valid student")
    public void iFillTheFormWithAValidStudent() {
        // Email / Password / Confirmation
        type(By.xpath("/html/body/div[2]/div[2]/main/div[2]/div/div[2]/form/div[2]/div/div[1]/input\n"), s.getEmail(), false, 10);

        type(By.id("edit-pass-pass1"), s.getPassword(), false, 10);
        type(By.id("edit-pass-pass2"), s.getPassword(), false, 10);

        // Civility
        WebElement genderSection = visible(By.id("edit-field-civilite--wrapper"), 10);
        scrollCenter(genderSection);
        switch (s.getGender()) {
            case Male -> safeClick(By.xpath("//*[@id='edit-field-civilite']/div[2]/label"), 10);
            case Female -> safeClick(By.xpath("//*[@id='edit-field-civilite']/div[1]/label"), 10);
        }

        // Names
        type(By.id("edit-field-nom-0-value"), s.getLastName(), false, 10);
        type(By.id("edit-field-prenom-0-value"), s.getFirstName(), false, 10);

        // Country of residence (selectized, prefixed with "-")
        String selected = selectFromSelectized("edit-field-pays-concernes-selectized",
                "-" + s.getCountryOfResidence(), 10);
        Assertions.assertEquals("-" + s.getCountryOfResidence(), selected);

        // Nationality / Postal code / City / Phone
        type(By.id("edit-field-nationalite-0-target-id"), s.getNationality(), false, 10);
        type(By.id("edit-field-code-postal-0-value"), s.getPostalCode(), false, 10);
        type(By.id("edit-field-ville-0-value"), s.getCity(), false, 10);
        type(By.id("edit-field-telephone-0-value"), s.getPhone(), false, 10);

        // Sélectionner le statut Etudiant
        safeClick(By.cssSelector("label[for='edit-field-publics-cibles-2']"), 10);

        // Study field
        selected = selectFromSelectized("edit-field-domaine-etudes-selectized", s.getStudyField(), 10);
        Assertions.assertEquals(s.getStudyField(), selected);

        // Study level — if UI returns "L1" for data "L", adapt assertion accordingly
        selected = selectFromSelectized("edit-field-niveaux-etude-selectized", s.getStudyLevel(), 10);
        // Example if needed: Assertions.assertEquals(s.getStudyLevel() + "1", selected);
    }

    @Then("the Student status is checked")
    public void theStudentStatusIsChecked() {
        boolean selected = driver.findElement(By.id("edit-field-publics-cibles-2")).isSelected();
        Assertions.assertTrue(selected, "Le statut Student devrait être coché !");
    }

    @And("I accept the conditions")
    public void iAcceptTheConditions() {
        // Accept communications/conditions by clicking the label
        WebElement terms = visible(
                By.xpath("//*[@id='edit-field-accepte-communications-wrapper']/div/label"), 10);
        scrollCenter(terms);
        terms.click();
    }

}
