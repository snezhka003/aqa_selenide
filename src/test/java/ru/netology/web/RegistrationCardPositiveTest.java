package ru.netology.web;

import com.codeborne.selenide.*;
import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.Selenide.*;

public class RegistrationCardPositiveTest {
    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static boolean checkArray(String[] array, String regex) {
        // Создаем объект Pattern для регулярного выражения
        Pattern pattern = Pattern.compile(regex);

        // Перебираем все элементы массива
        for (String element : array) {
            // Создаем matcher для текущего элемента
            Matcher matcher = pattern.matcher(element);

            // Если элемент не соответствует регулярному выражению
            if (!matcher.matches()) {
                return false; // Возвращаем false сразу
            }
        }
        return true; // Все элементы соответствуют
    }

    @BeforeEach
    void openBrowser() {
        open("http://localhost:9999");
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }

    @Test
    void shouldBeDefaultDateMoreThenCurrentDateForThreeDays() {
        String defaultDate = generateDate(3, "dd.MM.yyyy");

        $("[data-test-id='date'] input.input__control").should(Condition.value(defaultDate));
    }

    @Test
    void shouldBeRegisterByAccountNumber() {
        String planningDate = generateDate(5, "dd.MM.yyyy");

        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='date'] input.input__control")
                .doubleClick()
                .press(Keys.DELETE)
                .setValue(planningDate);
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
//        $(Selectors.withText("Успешно!")).should(Condition.visible, Duration.ofSeconds(15));
//        $(Selectors.withText("Встреча успешно забронирована на " + planningDate)).should(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id='notification'] div.notification__title").should(Condition.text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] div.notification__content").should(Condition.text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldBeRegisterByAccountNumberWithSpecialSymbol_ё() {
        String planningDate = generateDate(5, "dd.MM.yyyy");

        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='date'] input.input__control")
                .doubleClick()
                .press(Keys.DELETE)
                .setValue(planningDate);
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артём");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='notification'] div.notification__title").should(Condition.text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] div.notification__content").should(Condition.text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldBeAllElementsInDropdownContainInputSymbols() {
        $("[data-test-id='city'] input.input__control").setValue("Кр"); //вводим две буквы в поле города
        ElementsCollection elements = $$(".popup__container span.menu-item__control"); //получаем коллекцию элементов - городов в выпадающем списке
        List<String> texts = elements.texts(); //получаем лист текстов всех элементов коллекции
        String[] textsArray = texts.toArray(new String[0]); //преобразуем лист в массив
        String regex = "(.*[кК]+.*[рР]+.*)"; //регулярное выражение для проверки текстов в массиве на соответствие ему

        //проверяем, что тексты всех элементов массива соответствуют регулярному выражению
        Assert.isTrue(checkArray(textsArray, regex), "Не все элементы списка соответствуют условию");

        //пыталась средствами Selenide реализовать проверку, но не смогла
        //elements.shouldBe(allMatch("asdf", (Predicate<WebElement>) element("span.menu-item__control").getText().contains());
    }

    @Test
    void shouldBeFindElementInDropdownContainInputSymbols() {
        $("[data-test-id='city'] input.input__control").setValue("Кр");
        $$(".popup__container span.menu-item__control").findBy(Condition.text("Екатеринбург")).click();
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='notification'] div.notification__title").should(Condition.text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] div.notification__content").should(Condition.text("Встреча успешно забронирована на " + $("[data-test-id='date'] input.input__control").getText()));
    }

    @Test
    void shouldBeFindElementInWidgetCalendar() {
        int countPlanningDays = 7; //кол-во дней, которое нужно прибавить к текущей дате, согласно условию ДЗ № 2
        String planningDate = generateDate(countPlanningDays, "dd.MM.yyyy"); //Выбор даты на неделю вперёд, начиная от текущей даты

        $("[data-test-id='city'] input.input__control").setValue("Краснодар");
        $("[data-test-id='date'] input.input__control").click(); //кликаем на поле даты для отображения виджета календаря

        //получаем коллекцию элементов - всех дней, доступных для выбора в текущем месяце, в появившемся виджете календаря
        ElementsCollection activeDates = $$(".calendar__layout [data-day]");

        //вычисляем кол-во дней, заблокированных для выбора в виджете: первая доступная лдя выбора дата минус текущая дата
        int countInactiveDaysFromCurrentDate = Integer.parseInt($(".calendar__layout .calendar__day_state_current").getText()) - Integer.parseInt($(".calendar__layout .calendar__day_state_today").getText());

        //вычисляем кол-во доступных для выбора дней в заданном в переменной countPlanningDays периоде: вычитаем из него кол-во заблокированных для выбора дней
        int countPlanningActiveDays = countPlanningDays - countInactiveDaysFromCurrentDate;
        int remains;  //остаток, который вычисляем в следующем за текущим месяце, если будет нужно переключиться на него
        int currentWeek = activeDates.size(); //количество элементов - дней, доступных для выбора в текущем месяце

        if (currentWeek < countPlanningActiveDays) { //если дней, доступных для выбора в текущем месяце, недостаточно для получения нужной даты planningDate
            remains = countPlanningActiveDays - currentWeek; //то вычисляем остаток - нужный нам день, учитывая заданный период, в новом месяце
            $(".calendar__title [data-step='1']").click(); // и переключаемся на следующий месяц
            activeDates.get(remains).click(); //выбираем нужный нам день в новом месяце и кликаем на него, тем самым заполнив поле даты
        } else { //если дней, доступных для выбора в текущем месяце, достаточно для получения нужной даты planningDate
            activeDates.get(countPlanningActiveDays).click(); //выбираем нужную дату, исходя из заданного периода
        }

        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='notification'] div.notification__title").should(Condition.text("Успешно!"), Duration.ofSeconds(15));
        $("[data-test-id='notification'] div.notification__content").should(Condition.text("Встреча успешно забронирована на " + planningDate));
    }
}
