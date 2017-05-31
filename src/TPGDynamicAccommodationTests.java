import static org.junit.Assert.*;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.firefox.*;


public class TPGDynamicAccommodationTests 
{
	static WebDriver driver;
	static WebElement w;
	static Actions actions;
	static WebDriverWait wait;
	
	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setUp() throws Exception 
	{
		//Initialize driver
		File binaryFile = new File(TestData.FIREFOX_PATH);
		FirefoxBinary ffBinary = new FirefoxBinary(binaryFile);
		FirefoxProfile ffProfile = new FirefoxProfile();
		System.setProperty("webdriver.gecko.driver", TestData.GECKO_DRIVER_PATH);
		driver = new FirefoxDriver(ffBinary, ffProfile);
		wait = new WebDriverWait(driver, 10);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		//Used for more complex actions (drag-and-drop etc.)
		actions = new Actions(driver);
		
		//TPG Login
	    driver.get(TestData.TPG_ACC_TEST_URL);
	    driver.findElement(By.name("login_name")).clear();
	    driver.findElement(By.name("login_name")).sendKeys(TestData.TPG_USERNAME);
	    driver.findElement(By.name("login_password")).clear();
	    driver.findElement(By.name("login_password")).sendKeys(TestData.TPG_PASSWORD);
	    driver.findElement(By.name("enter")).click();
	}
	
	@Test
	public void CreateNewDestinationTest() throws InterruptedException
	{
	    
//	    Click on the globe icon 
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector( "td[title='Geography']"))).click();
//	    Selects the continent(starting from the main frame and going down to the needed element)
		driver.switchTo().defaultContent();
		driver.switchTo().frame("content");
		driver.switchTo().frame("tvcTabs0contentFrame");
		driver.switchTo().frame("tableContentFrame");			   
		driver.switchTo().frame("tableBodyRight");
//		Select "Europe" if it's not selected already
		WebElement europeCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[value*='|13440.54920.12832.63885']")));
																															
		if(!europeCheckbox.isSelected())
			europeCheckbox.click();

//		Expands the continent Europe
		if( !(driver.findElements(By.xpath("//td[contains(text(),'Austria')]")).size() > 0) ) //Europe is not expanded already, click + to expand it
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table[id='tbl'] tbody tr:nth-of-type(3) td:nth-of-type(2) table tbody tr:nth-of-type(2)"))).click();
		
//		Check if Country Destination exists already and add it if doesn't
		boolean countryExistsAlready = driver.findElements(By.xpath("//td[contains(text(),'"+TestData.COUNTRY1+"')]")).size() > 0;
		
		if(!countryExistsAlready)
		{
			
			//After the country is selected this step will choose "New Country" from the drop down menu
			driver.switchTo().parentFrame();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body[class='content'] ul li:nth-of-type(1)"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.uip-menu li:nth-of-type(2)"))).click();
			//Fills the form with country name, ISO code and Haul
			driver.switchTo().parentFrame();
			driver.switchTo().frame("tvcInlineFrame_0");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#f74"))).sendKeys(TestData.COUNTRY1);  
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#f75"))).sendKeys(TestData.COUNTRY1_ISO_CODE);
			//Submit
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img#button-done"))).click();
		}
		
		//Test that the country now exists (regardless if it was added by the script or pre-existed)
		driver.switchTo().defaultContent();
		driver.switchTo().frame("content");
		driver.switchTo().frame("tvcTabs0contentFrame");
		driver.switchTo().frame("tableContentFrame");			   
		driver.switchTo().frame("tableBodyRight");
		WebElement countryCheckbox = (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(text(),'"+TestData.COUNTRY1+"')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", countryCheckbox);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='13440.54920.58928.49357|13440.54920.58928.58805|13440.54920.12832.63885|23']"))).click();//"Macedonia" hard-coded
		assertTrue(driver.findElements(By.xpath("//td[contains(text(),'"+TestData.COUNTRY1+"')]")).size() > 0);
		
	 }

//	}
//	@Test
//	public void MapTheHotelToDestination() throws InterruptedException
//	{
//		assertTrue(true);
//	}
//	@Test
//	public void MapTheHotelToSeason() throws InterruptedException
//	{
//		assertTrue(true);
//	}
//	@Test
//	public void CreateConfigurationForTheHotel() throws InterruptedException
//	{
//		assertTrue(true);
//	}
	
	@AfterClass
	public static void tearDown() throws Exception
	{
		//driver.quit();
	}	
}
