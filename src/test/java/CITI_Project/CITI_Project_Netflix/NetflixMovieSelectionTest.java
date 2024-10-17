package CITI_Project.CITI_Project_Netflix;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class NetflixMovieSelectionTest {
	
	
    static final Logger logger = Logger.getLogger(NetflixMovieSelectionTest.class);
    WebDriver driver;
    WebDriverWait wait;
    boolean executionOnce = true;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setUp() {
    	 ExtentSparkReporter spark = new ExtentSparkReporter("ExtentReport.html");
        spark.config().setReportName("Netflix Automation results");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "Krithika");
        test = extent.createTest("Netflix Movie Selection Test");
        
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.netflix.com/login");
        
        //User Your Netlfix Email in sendkeys 
        driver.findElement(By.id(":r0:")).sendKeys("//Enter Your Netflix Emmail");
        //Use Your Netflix password in sendkeys
        driver.findElement(By.id(":r3:")).sendKeys("//Enter Your Netflix password");
        driver.findElement(By.xpath("//button[contains(@class,' e1ax5wel2')][1]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='profile-link']//span[text()='kiki']")));
        driver.findElement(By.xpath("//a[@class='profile-link']//span[text()='kiki']")).click();
        
        test.log(Status.INFO, "Login successful and profile selected.");
    }

    @Test(dataProvider = "movieData")
    public void addMoviesToMyList(String movieToSelect, String searchMovie) throws InterruptedException {
        test.log(Status.INFO, "Adding movie: " + movieToSelect + " with search keyword: " + searchMovie);
        
        if (executionOnce) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("searchTab")));
            driver.findElement(By.className("searchTab")).click();
            executionOnce = false;
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.findElement(By.id("searchInput")).sendKeys(searchMovie);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[text()='" + movieToSelect + "']")));

        WebElement movieName = driver.findElement(By.xpath("//p[text()='" + movieToSelect + "']"));
        if (movieName != null) {
            driver.findElement(By.xpath("//p[text()='" + movieToSelect + "']//preceding::img[1]")).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));

            WebElement element = driver.findElement(By.xpath("//div[@class='buttonControls--container']//div[contains(@class,'ltr-bjn8wh')]//div[@class='ptrack-content']//button[contains(@class,'color-supplementary')]"));

            String dataUiaValue = element.getAttribute("data-uia");
            if (dataUiaValue.equalsIgnoreCase("add-to-my-list")) {
                test.log(Status.PASS, "Successfully added movie: " + movieToSelect + " to My List");
                element.click();
            } else {
                test.log(Status.FAIL, "Movie: " + movieToSelect + " was already in My List.");
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='previewModal-close']")));
            driver.findElement(By.xpath("//div[@class='previewModal-close']")).click();
            driver.findElement(By.id("searchInput")).clear();
        } else {
            test.log(Status.FAIL, "Movie: " + movieToSelect + " not found.");
        }
    }

    @Test(dependsOnMethods = "addMoviesToMyList")
    public void verifyMoviesInMyList() {
        WebElement MyList = driver.findElement(By.xpath("//div[@class='pinning-header']//li[@class='navigation-tab']/a[text()='My List']"));
        MyList.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));

        List<WebElement> moviesList = driver.findElements(By.xpath("//div[@class='galleryContent']//div[@class='galleryLockups']"
        		+ "//div[@class='sliderContent row-with-x-columns']//div[contains(@class,'slider-item')]//div[@class='fallback-text-container']//p"));

        String[] moviesToVerify = {"GOAT - The Greatest of All Time", "Inception", "Venom: Let There Be Carnage"};
        boolean allMoviesAdded = true;

        for (String movie : moviesToVerify) {
            boolean movieFound = false;
            for (WebElement movieElement : moviesList) {
                if (movieElement.getText().equalsIgnoreCase(movie)) {
                    test.log(Status.PASS, "Movie found in My List: " + movie);
                    movieFound = true;
                    break;
                }
            }
            if (!movieFound) {
                allMoviesAdded = false;
                test.log(Status.FAIL, "Movie not found in My List: " + movie);
            }
        }
        Assert.assertTrue(allMoviesAdded, "Some movies were not found in My List.");
    }

    @DataProvider(name = "movieData")
    public Object[][] getMovieData() {
        return new Object[][] {
            {"GOAT - The Greatest of All Time", "goat"},
            {"Inception", "inception"},
            {"Venom: Let There Be Carnage", "venom"}
        };
    }

    @AfterClass
    public void logOut() {
        driver.findElement(By.className("account-dropdown-button")).click();
        driver.findElement(By.xpath("//div[contains(@class,'ptrack-content')]//a[contains(text(),'Sign out of Netflix')]")).click();
        driver.quit();
        
        test.log(Status.INFO, "Test execution completed and browser closed.");
        extent.flush();
    }
}
