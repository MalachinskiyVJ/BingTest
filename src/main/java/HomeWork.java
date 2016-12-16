import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

/**
 * Created by Вова on 16.12.2016.
 */
public class HomeWork {
    public static void main(String[] args) {

        WebDriver driver = new FirefoxDriver();
        //1. Открыть главную страницу поисковой системы Bing.
        driver.get("https://www.bing.com/");

        //2. Перейти в раздел поиска изображений. Дождаться, что заголовок страницы имеет название “Лента изображений Bing.”
        WebElement imagesfilter = driver.findElement(By.xpath("//a[@id='scpl1']"));

        imagesfilter.click();
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.titleIs("Bing Image Feed"));

        //3. Выполнить прокрутку страницы несколько раз. Каждый раз проверять, что при достижении низа страницы подгружаются новые блоки с изображениями
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollBy(0,500)", "");
        List<WebElement> firstscroll = (new WebDriverWait(driver, 10)).
                until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("mimg")));
        System.out.println("The number of search elements after first scroll: " + firstscroll.size());

        js.executeScript("window.scrollBy(0,2000)", "");
        List<WebElement> secondscroll = (new WebDriverWait(driver, 10)).
                until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("mimg")));
        System.out.println("The number of search elements after second scroll: " + secondscroll.size());

        js.executeScript("window.scrollBy(0,-2500)", "");
        List<WebElement> scrollup = (new WebDriverWait(driver, 10)).
                until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("mimg")));

        /*4. В поисковую строку ввести слово без последней буквы. Дождаться появления слова целиком в выпадающем списке
        предложений. Выбрать искомое слово и дождаться загрузки результатов поискового запроса.*/
        if (firstscroll.size() < secondscroll.size()) {
            WebElement search = driver.findElement(By.className("b_searchbox"));
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(search));
            search.sendKeys("Seleniu");
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='sw_as']/div")));
            WebElement fullword = driver.findElement(By.xpath(".//*[@id='sa_ul']/li[1]/div"));
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(fullword));
            fullword.click();
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.id("vm_c")));

            //5. Установить фильтр Дата: “В прошлом месяце”. Дождаться обновления результатов.
            WebElement date = driver.findElement(By.xpath(".//*[@id='ftrB']/ul/li[6]/span"));
            date.click();
            WebElement pastmonth = driver.findElement(By.xpath("//a[contains(@title, 'Past month')]"));
            pastmonth.click();
            List<WebElement> seleniumresults = (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(".//*[@id='dg_c']")));

            //6. Нажать на первое изображение из результатов поиска. Дождаться перехода в режим слайд шоу.
           Actions action = new Actions(driver);
            WebElement firstimage = driver.findElement(By.xpath(".//*[@id='dg_c']/div[1]/div/div[1]/div/a/img"));
            (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.elementToBeClickable(firstimage));
            action.moveToElement(firstimage).build().perform();
            (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.elementToBeClickable(By.xpath("//img[@role='presentation']")));
            WebElement detailfirstimage = driver.findElement(By.xpath("//img[@role='presentation']"));
            detailfirstimage.click();

            /*7. Выполнить переключение на следующее, предыдущее изображение. После переключения между изображениями
            необходимо дожидаться обновления очереди изображений для показа в нижней части окна слайд шоу.*/

            driver.switchTo().frame(0);

            WebElement next = driver.findElement(By.xpath("//span[@id='view_image_main']//a[@id='iol_navr']"));
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(next));
            next.click();
            (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(".//*[@id='iol_fsc']/div[1]/a[2]/div/img")));

            WebElement previous = driver.findElement(By.xpath("//span[@id='view_image_main']//a[@id='iol_navl']"));
            previous.click();

            /*8. Нажать на отображаемое изображение в режиме слайд шоу и удостовериться, что
            картинка загрузилась в отдельной вкладке/окне.*/
            String originalWindow = driver.getWindowHandle();
            final Set<String> oldWindowsSet = driver.getWindowHandles();

            WebElement mainImage = driver.
                    findElement(By.xpath("//span[@id='view_image_main']//div[contains(@class, 'iol_imc')]//img[contains(@class, 'accessible') and contains(@class, 'mainImage')]"));
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(mainImage));
            mainImage.click();
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(mainImage));

            String newWindow = (new WebDriverWait(driver, 10))
                    .until(new ExpectedCondition<String>() {
                               public String apply(WebDriver driver) {
                                   Set<String> newWindowsSet = driver.getWindowHandles();
                                   newWindowsSet.removeAll(oldWindowsSet);
                                   return newWindowsSet.size() > 0 ?
                                           newWindowsSet.iterator().next() : null;
                               }
                           }
                    );

            driver.switchTo().window(newWindow);

            System.out.println("New window title: " + driver.getTitle());
            driver.close();

            driver.switchTo().window(originalWindow);
            System.out.println("Old window title: " + driver.getTitle());


            driver.switchTo().defaultContent();
            driver.quit();
        }
    }
}
