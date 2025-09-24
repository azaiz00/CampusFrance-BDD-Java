package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.function.Function;

public class SeleniumHelpers {

    /* ==== WAITs de base ==== */

    // Crée un WebDriverWait avec timeout en secondes
    public static WebDriverWait wait(WebDriver driver, long timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
    }

    // Attend que document.readyState == "complete"
    public static void waitForPageReady(WebDriver driver, long timeoutSec) {
        WebDriverWait w = wait(driver, timeoutSec);
        w.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState"))
        );
    }

    /* ==== Localisation robuste ==== */

    // Trouve un élément en attendant sa présence dans le DOM
    public static WebElement findPresent(WebDriver driver, By locator, long timeoutSec) {
        return wait(driver, timeoutSec).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Attend que l’élément soit visible
    public static WebElement findVisible(WebDriver driver, By locator, long timeoutSec) {
        return wait(driver, timeoutSec).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Attend que l’élément soit cliquable
    public static WebElement findClickable(WebDriver driver, By locator, long timeoutSec) {
        return wait(driver, timeoutSec).until(ExpectedConditions.elementToBeClickable(locator));
    }

    /* ==== Actions sûres ==== */

    // Scroll au centre (évite header sticky)
    public static void scrollIntoViewCenter(WebDriver driver, WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'})", el
        );
    }

    // JS click (fallback en cas d’overlay)
    public static void jsClick(WebDriver driver, WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    // Click robuste: visible → scroll → clickable → click ; fallback JS si intercepté
    public static void safeClick(WebDriver driver, By locator, long timeoutSec) {
        WebElement el = findVisible(driver, locator, timeoutSec);
        scrollIntoViewCenter(driver, el);
        try {
            findClickable(driver, locator, timeoutSec).click();
        } catch (ElementClickInterceptedException e) {
            jsClick(driver, el);
        }
    }

    // Saisie avec nettoyage + Tab optionnel
    public static void type(WebDriver driver, By locator, String text, boolean sendTab, long timeoutSec) {
        WebElement el = findVisible(driver, locator, timeoutSec);
        scrollIntoViewCenter(driver, el);
        el.clear();
        el.sendKeys(text);
        if (sendTab) el.sendKeys(Keys.TAB);
    }

    /* ==== Sélecteurs pratiques ==== */

    // label[for="<id>"] → click (utile pour radios/checkbox)
    public static void clickLabelFor(WebDriver driver, String inputId, long timeoutSec) {
        By labelFor = By.cssSelector("label[for='" + inputId + "']");
        safeClick(driver, labelFor, timeoutSec);
    }

    // Xpath par texte normalisé (évite espaces parasites)
    public static By byExactText(String tag, String text) {
        return By.xpath("//" + tag + "[normalize-space(text())='" + text + "']");
    }

    /* ==== Dropdown "selectized" (CampusFrance) ==== */

    // Sélectionne une option dans les dropdowns custom (selectized)
    public static String selectFromSelectized(WebDriver driver, String dropdownId, String optionText, long timeoutSec) {
        WebElement input = findVisible(driver, By.id(dropdownId), timeoutSec);
        scrollIntoViewCenter(driver, input);
        input.click();

        By optionBy = By.xpath("//div[@class='option' and normalize-space(text())='" + optionText + "']");
        WebElement option = findPresent(driver, optionBy, timeoutSec);
        scrollIntoViewCenter(driver, option);
        option.click();

        WebElement selectedItem = input.findElement(By.xpath("..//div[contains(@class,'item')]"));
        return selectedItem.getText().trim();
    }

    /* ==== Cookies banner spécifique (tarteaucitron) ==== */

    // Ferme la bannière cookies si présente (id connu sur le site)
    public static void closeCookiesIfPresent(WebDriver driver, long timeoutSec) {
        try {
            WebDriverWait shortWait = wait(driver, Math.min(timeoutSec, 5));
            WebElement btn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(By.id("tarteaucitronAllDenied2"))
            );
            scrollIntoViewCenter(driver, btn);
            btn.click();
        } catch (TimeoutException | NoSuchElementException | ElementClickInterceptedException ignored) {
            // pas de bannière ou pas cliquable → on continue
        }
    }

    /* ==== Vérifications utiles ==== */

    // Vérifie qu’un input (radio/checkbox) est coché
    public static void assertSelected(WebDriver driver, By locator, long timeoutSec) {
        WebElement el = findPresent(driver, locator, timeoutSec);
        if (!el.isSelected()) {
            throw new AssertionError("L’élément n’est pas sélectionné : " + locator);
        }
    }
}
