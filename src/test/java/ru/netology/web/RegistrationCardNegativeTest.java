package ru.netology.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationCardNegativeTest {
    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
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
    void shouldBeFailedRegisterByEmptyCity() {
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='city'].input_invalid span.input__sub").should(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldBeFailedRegisterByEmptyDate() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='date'] input.input__control")
                .doubleClick()
                .press(Keys.DELETE);
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='date'] span.input_invalid span.input__sub").should(Condition.text("Неверно введена дата"));
    }

    @Test
    void shouldBeFailedRegisterByEmptyName() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='name'].input_invalid span.input__sub").should(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldBeFailedRegisterByEmptyPhone() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='phone'].input_invalid span.input__sub").should(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldBeFailedRegisterByEmptyCheckbox() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='name'] input.input__control").setValue("Петров-Водкин Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("button.button").click();
        $("[data-test-id='agreement'].input_invalid span.checkbox__text").should(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

    @Test
    void shouldBeFailedRegisterByInvalidCity() {
        $("[data-test-id='city'] input.input__control").setValue("Котлас");
        $("[data-test-id='name'] input.input__control").setValue("Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='city'].input_invalid span.input__sub").should(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldBeFailedRegisterByDateLessThenThreeDaysAwayFromCurrentDate() {
        String planningDate = generateDate(2, "dd.MM.yyyy");

        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='date'] input.input__control")
                .doubleClick()
                .press(Keys.DELETE)
                .setValue(planningDate);
        $("[data-test-id='name'] input.input__control").setValue("Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='date'] span.input_invalid span.input__sub").should(Condition.text("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldBeFailedRegisterByInvalidDate() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='date'] input.input__control")
                .doubleClick()
                .press(Keys.DELETE)
                .setValue("30.02.2025");
        $("[data-test-id='name'] input.input__control").setValue("Артем");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='date'] span.input_invalid span.input__sub").should(Condition.text("Неверно введена дата"));
    }

    @Test
    void shouldBeFailedRegisterByInvalidName() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='name'] input.input__control").setValue("Petrov");
        $("[data-test-id='phone'] input.input__control").setValue("+79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='name'].input_invalid span.input__sub").should(Condition.text("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldBeFailedRegisterByInvalidPhone() {
        $("[data-test-id='city'] input.input__control").setValue("Биробиджан");
        $("[data-test-id='name'] input.input__control").setValue("Артем");
        $("[data-test-id='phone'] input.input__control").setValue("79001234567");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $("[data-test-id='phone'].input_invalid span.input__sub").should(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }
}
