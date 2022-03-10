package testUtility;

import com.github.javafaker.Faker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TestUtility {
    //time out data
    private int timeOut = Integer.parseInt(ConfigurationReader.getDataFromPropertiesFile(
            "config.properties","timeout"));

    WebDriver driver;
    public TestUtility(WebDriver driver){
        this.driver = driver;
    }

    //Sleep Function
    public void sleep(int seconds){
        try{
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //Wait For Element Function
    public void waitForElementPresent(WebElement element){
        WebDriverWait wait = new WebDriverWait(driver,timeOut);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    //Wait For Alert Function
    public void waitForAlertPresent(){
        WebDriverWait wait = new WebDriverWait(driver,timeOut);
        wait.until(ExpectedConditions.alertIsPresent());
    }

    //Wait For Element Clickable Function
    public void waitForElementClickable(WebElement element){
        WebDriverWait wait = new WebDriverWait(driver,timeOut);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    //*********Generate Random Data Using Java Faker***********
    public static String generateFakeTitle(){
        Faker faker = new Faker();
        return faker.name().title();
    }

    public static String generateFakeName(){
        Faker faker = new Faker();
        return faker.name().name();
    }

    public static String generateFakeUserName(){
        Faker faker = new Faker();
        return faker.name().username();
    }

    public static String generateFakeFirstName(){
        Faker faker = new Faker();
        return faker.name().firstName();
    }

    public static String generateFakeLastName(){
        Faker faker = new Faker();
        return faker.name().lastName();
    }

    public static String generateFakeNumber(){
        Faker faker = new Faker();
        return faker.phoneNumber().subscriberNumber();
    }

    //*****************************************************

    //Generate Random Email Address
    public static String randomEmailAddress(){
        String generatedEmail = RandomStringUtils.randomAlphabetic(7);
        return generatedEmail + "@gmail.com";
    }

    //Generate Random Code
    public static String randomCode(){
        String code = "mg" + RandomStringUtils.randomNumeric(8);
        return code;
    }

    //Generate Random Password
    public static String generateRandomPassword(){
        return RandomStringUtils.randomAlphabetic(8);
    }


    //****************************************************

    //Read From Excel File
    public static String readFromExcel(String fileName,String sheetName,int rowNumber,int columnNumber){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XSSFSheet sheet = workbook.getSheet(sheetName);
        XSSFRow row = sheet.getRow(rowNumber);

        String cellValue = null;
        if (row == null){
            System.out.println("Empty row, there is no data in the excel sheet");
        }else{
            XSSFCell cell = row.getCell(columnNumber);
            CellType cellType = cell.getCellType();
            switch (cellType){
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
            }
        }
        return cellValue;
    }

    public static List<String> readMultipleCellValueFromExcel(String fileName, String sheetName, String startRowName){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sheets = workbook.getNumberOfSheets();
        List<String> values = new ArrayList<>();
        for (int i = 0;i < sheets;i++){
            if (workbook.getSheetName(i).equalsIgnoreCase(sheetName)){
                XSSFSheet sheet = workbook.getSheetAt(i);

                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()){
                    Row row = rows.next();
                    if (row.getCell(i).getStringCellValue().equalsIgnoreCase(startRowName)){
                        Iterator<Cell> cells = row.cellIterator();
                        while (cells.hasNext()){
                            Cell cell = cells.next();
                            if (cell.getCellTypeEnum() == CellType.STRING){
                                values.add(cell.getStringCellValue());
                            }else {
                                values.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                            }
                        }
                    }
                }
            }
        }
        return values;
    }

    //************Take Screen Shot Function*******************

    public static void takeScreenShot(String folder,String fileName,WebDriver driver){
        DateTime dateTime = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss");
        String timeStamp = dateTime.toString(formatter);
        fileName = fileName + timeStamp;
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File screenShotFile = screenshot.getScreenshotAs(OutputType.FILE);
        File finalFile = new File(folder + File.separator + fileName + ".png");
        try {
            FileUtils.copyFile(screenShotFile,finalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //**************Read From CSV File*********************
    public static String readFromCSVFile(String folder,String fileName,String headerName){
        Reader reader = null;
        try {
            reader = new FileReader(folder + File.separator + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CSVParser parser = null;
        try {
            parser = CSVFormat.RFC4180.withDelimiter(',').withHeader().parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String value = null;
        for (CSVRecord record : parser){
            value = record.get(headerName);
            break;
        }
        return value;
    }

    //Read All Lines From CSV
    public static List<List<String>> readAllLinesFromCSV(String folder, String file) {
        file = folder + File.separator + file;
        String delimiter = ",";
        String line;
        List<List<String>> lines = new ArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                List<String> values = Arrays.asList(line.split(delimiter));
                lines.add(values);
            }
            lines.forEach(l -> System.out.println(l));
        } catch (Exception e) {
            System.out.println(e);
        }

        return lines;
    }
}