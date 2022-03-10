package testUtility;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class TestBase {
    public static WebDriver driver;
    public static String configFile = "config.properties";
    public static String sysName = System.getProperty("os.name");


    //Read  Key Value Directly From Config File
    public static Properties properties = new Properties();
    static {
        String workingDirectory = System.getProperty("user.dir");
        try {
            properties.load(new FileInputStream(workingDirectory + File.separator + configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Initialize ChromeDriver To Every Operating System: Windows,IOS,Linux
    public static void initialization(String urlKey){
        //Singleton Pattern
        if (driver == null){
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            System.out.println("Operating System: " + sysName);
            if (sysName.toLowerCase().contains("windows")){
                System.out.println("Test Running On Windows Mode");
                System.setProperty("webdriver.chrome.driver", "c:\\webdriver\\chromedriver.exe");
            }else if (sysName.toLowerCase().contains("mac")){
                System.out.println("Test Running On Mac Mode");
                System.setProperty("webdriver.chrome.driver", "/Applications/chromedriver");
            }else if (sysName.toLowerCase().contains("linux")){
                System.out.println("Test Running On Headless Mode");
                //System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
                WebDriverManager.chromedriver().setup();
            }
            if (useHeadless()){
                System.out.println("Headless Mode Initialized");
                chromeOptions.addArguments("windows-size=1200,1100");
                chromeOptions.addArguments(Arrays.asList
                        ("--start--maximized","--allow-insecure-localhost","--headless","--disable-gpu"));
            }
            driver = new ChromeDriver(chromeOptions);
            driver.manage().window().maximize();
            driver.get(properties.getProperty(urlKey));
        }
    }


    public static boolean useHeadless(){
        return Integer.parseInt(ConfigurationReader.getDataFromPropertiesFile(configFile,"headlessMode")) == 1;
    }

    public static void closeBrowser(){
        driver.close();
        driver.quit();
        driver = null;
    }
}