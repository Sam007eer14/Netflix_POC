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

import io.github.bonigarcia.wdm.WebDriverManager;

public class NetflixUserActionsTest {


		public static void main(String[] args) throws InterruptedException {
			
			String movieToSelect = "GOAT - The Greatest of All Time";
			String searchMovie = "goat";
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-notifications");
			WebDriver driver = new ChromeDriver(options);
			driver.get("https://www.netflix.com/login");
			driver.manage().window().maximize();
			// Login Credentials
			driver.findElement(By.id(":r0:")).sendKeys("krithika650@gmail.com");
			driver.findElement(By.id(":r3:")).sendKeys("tccooool");
			driver.findElement(By.xpath("//button[contains(@class,' e1ax5wel2')][1]")).click();
			
		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
		    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='profile-link']//span[text()='kiki']")));
		   
			driver.findElement(By.xpath("//a[@class='profile-link']//span[text()='kiki']")).click();
			 wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("searchTab")));
			driver.findElement(By.className("searchTab")).click();
					
			driver.findElement(By.id("searchInput")).sendKeys(searchMovie);
			 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[text()='" + movieToSelect + "']")));
			WebElement movieName = driver.findElement(By.xpath("//p[text()='" + movieToSelect + "']"));
			if (movieName != null) {

				driver.findElement(By.xpath("//p[text()='GOAT - The Greatest of All Time']//preceding::img[1]")).click();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
				 WebElement element = driver.findElement(By.xpath("//div[@class='buttonControls--container']//div[contains(@class,'ltr-bjn8wh')]//div[@class='ptrack-content']//button[contains(@class,'color-supplementary')]"));

			     
			        String dataUiaValue = element.getAttribute("data-uia");
	                if(dataUiaValue.equalsIgnoreCase("add-to-my-list")){
	                	  System.out.println("Value of data-uia attribute: " + dataUiaValue);
	          			driver.findElement(By.xpath(
	          					"//div[contains(@class,'previewModal--player-titleTreatment')]//div[contains(@class,'ptrack-content')]"))
	          					.click();
	                }
			        
			      
				 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='previewModal-close']")));
				driver.findElement(By.xpath("//div[@class='previewModal-close']")).click();
			}
			WebElement MyList = driver.findElement(
					By.xpath("//div[@class='pinning-header']//li[@class='navigation-tab']/a[text()='My List']"));
			MyList.click();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
			List<WebElement> moviesList = driver.findElements(By.xpath(
					"//div[@class='galleryContent']//div[@class='galleryLockups']//div[@class='sliderContent row-with-x-columns']//div[contains(@class,'slider-item')]//div[@class='fallback-text-container']//p"));
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
			boolean check = false;

			for (WebElement val : moviesList) {
				String selectedMovie = val.getText();

				if (selectedMovie.equalsIgnoreCase(movieToSelect)) {
					System.out.println("Movie has been successfully added");
					check = true;
					break;
				}
			}
			Assert.assertTrue(check, "The movie " + movieToSelect + " was not found in the list."); 
			driver.findElement(By.className("account-dropdown-button")).click();
			driver.findElement(
					By.xpath("//div[contains(@class,'ptrack-content')]//a[contains(text(),'Sign out of Netflix')]"))
					.click();

		
		
	}

}
