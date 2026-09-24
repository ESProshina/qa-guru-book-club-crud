package tests;

import io.qameta.allure.Allure;
import models.clubs.ClubModel;
import models.clubs.CreateClubBodyModel;
import models.clubs.PatchClubBodyModel;
import models.login.LoginBodyModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static specs.clubs.ClubsSpec.*;
import static tests.TestData.*;

public class ClubsCrudTests extends TestBase {

    private String accessToken;
    private Integer createdClubId;

    @BeforeEach
    public void auth() {
        Allure.step("Авторизация и получение access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            accessToken = api.auth.loginAndGetAccessToken(loginData);
        });
    }

    @AfterEach
    public void cleanup() {
        Allure.step("Cleanup: удаление созданного клуба", () -> {
            if (createdClubId != null) {
                try {
                    api.clubs.deleteClubWithSpec(accessToken, createdClubId, clubNotFoundResponseSpec);
                } catch (Exception ignored) { /* клуб уже удалён */ }
                try {
                    api.clubs.deleteClub(accessToken, createdClubId);
                } catch (Exception ignored) { /* клуб уже удалён */ }
                createdClubId = null;
            }
        });
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("Позитивный: Создание клуба (201 Created)")
    public void createClubTest() {
        CreateClubBodyModel body = new CreateClubBodyModel(
                CLUB_BOOK_TITLE,
                CLUB_BOOK_AUTHORS,
                CLUB_PUBLICATION_YEAR,
                CLUB_DESCRIPTION,
                CLUB_TELEGRAM_LINK
        );

        ClubModel response = Allure.step("POST /clubs/", () ->
                api.clubs.createClub(accessToken, body));
        createdClubId = response.id();

        Allure.step("Проверка: id > 0", () -> assertThat(response.id()).isPositive());
        Allure.step("Проверка: bookTitle", () -> assertThat(response.bookTitle()).isEqualTo(CLUB_BOOK_TITLE));
        Allure.step("Проверка: bookAuthors", () -> assertThat(response.bookAuthors()).isEqualTo(CLUB_BOOK_AUTHORS));
        Allure.step("Проверка: publicationYear", () -> assertThat(response.publicationYear()).isEqualTo(CLUB_PUBLICATION_YEAR));
        Allure.step("Проверка: description", () -> assertThat(response.description()).isEqualTo(CLUB_DESCRIPTION));
        Allure.step("Проверка: telegramChatLink", () -> assertThat(response.telegramChatLink()).isEqualTo(CLUB_TELEGRAM_LINK));
        Allure.step("Проверка: owner != null", () -> assertThat(response.owner()).isNotNull());
        Allure.step("Проверка: created != null", () -> assertThat(response.created()).isNotNull());
    }

    // ==================== READ ====================

    @Test
    @DisplayName("Позитивный: Получение клуба по ID (200 OK)")
    public void getClubByIdTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        createdClubId = created.id();

        ClubModel response = Allure.step("GET /clubs/" + createdClubId + "/", () ->
                api.clubs.getClubById(createdClubId));

        Allure.step("Проверка: id совпадает", () -> assertThat(response.id()).isEqualTo(createdClubId));
        Allure.step("Проверка: bookTitle", () -> assertThat(response.bookTitle()).isEqualTo(CLUB_BOOK_TITLE));
        Allure.step("Проверка: description", () -> assertThat(response.description()).isEqualTo(CLUB_DESCRIPTION));
    }

    // ==================== UPDATE (PUT) ====================

    @Test
    @DisplayName("Позитивный: Полное обновление клуба через PUT (200 OK)")
    public void updateClubPutTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        createdClubId = created.id();

        CreateClubBodyModel updated = new CreateClubBodyModel(
                UPDATED_CLUB_BOOK_TITLE,
                UPDATED_CLUB_BOOK_AUTHORS,
                UPDATED_CLUB_PUBLICATION_YEAR,
                UPDATED_CLUB_DESCRIPTION,
                UPDATED_CLUB_TELEGRAM_LINK
        );

        ClubModel response = Allure.step("PUT /clubs/" + createdClubId + "/", () ->
                api.clubs.updateClub(accessToken, createdClubId, updated));

        Allure.step("Проверка: bookTitle обновлён", () ->
                assertThat(response.bookTitle()).isEqualTo(UPDATED_CLUB_BOOK_TITLE));
        Allure.step("Проверка: bookAuthors обновлён", () ->
                assertThat(response.bookAuthors()).isEqualTo(UPDATED_CLUB_BOOK_AUTHORS));
        Allure.step("Проверка: description обновлён", () ->
                assertThat(response.description()).isEqualTo(UPDATED_CLUB_DESCRIPTION));
    }

    // ==================== UPDATE (PATCH) ====================

    @Test
    @DisplayName("Позитивный: Частичное обновление клуба через PATCH (200 OK)")
    public void updateClubPatchTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        createdClubId = created.id();

        PatchClubBodyModel patch = new PatchClubBodyModel(
                UPDATED_CLUB_BOOK_TITLE, null, null, null, null);

        ClubModel response = Allure.step("PATCH /clubs/" + createdClubId + "/", () ->
                api.clubs.patchClub(accessToken, createdClubId, patch));

        Allure.step("Проверка: bookTitle изменился", () ->
                assertThat(response.bookTitle()).isEqualTo(UPDATED_CLUB_BOOK_TITLE));
        Allure.step("Проверка: bookAuthors НЕ изменился", () ->
                assertThat(response.bookAuthors()).isEqualTo(CLUB_BOOK_AUTHORS));
        Allure.step("Проверка: description НЕ изменился", () ->
                assertThat(response.description()).isEqualTo(CLUB_DESCRIPTION));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Позитивный: Удаление клуба (204 No Content)")
    public void deleteClubTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        int id = created.id();

        Allure.step("DELETE /clubs/" + id + "/", () -> api.clubs.deleteClub(accessToken, id));

        Allure.step("Проверка: GET по удалённому ID возвращает 404", () -> {
            var response = api.clubs.getClubByIdWithSpec(id, clubNotFoundResponseSpec);
            assertThat(response.statusCode()).isEqualTo(404);
        });

        // клуб уже удалён, cleanup не нужен
        createdClubId = null;
    }

    // ==================== MEMBERS ====================

    @Test
    @DisplayName("Позитивный: Вступление в клуб (204 No Content)")
    public void joinClubTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        createdClubId = created.id();

        Allure.step("POST /clubs/" + createdClubId + "/members/me/", () ->
                api.clubs.joinClub(accessToken, createdClubId));

        Allure.step("Проверка: пользователь добавлен в members", () -> {
            ClubModel refreshed = api.clubs.getClubById(createdClubId);
            assertThat(refreshed.members()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("Позитивный: Выход из клуба (204 No Content)")
    public void leaveClubTest() {
        ClubModel created = api.clubs.createClub(accessToken,
                new CreateClubBodyModel(CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS,
                        CLUB_PUBLICATION_YEAR, CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK));
        createdClubId = created.id();

        Allure.step("Вступаем в клуб", () -> api.clubs.joinClub(accessToken, createdClubId));

        Allure.step("DELETE /clubs/" + createdClubId + "/members/me/", () ->
                api.clubs.leaveClub(accessToken, createdClubId));
    }

    // ==================== НЕГАТИВНЫЕ ====================

    @Test
    @DisplayName("Негативный: Создание клуба без токена (401 Unauthorized)")
    public void createClubWithoutTokenTest() {
        CreateClubBodyModel body = new CreateClubBodyModel(
                CLUB_BOOK_TITLE, CLUB_BOOK_AUTHORS, CLUB_PUBLICATION_YEAR,
                CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK);

        var response = Allure.step("POST /clubs/ без токена", () ->
                api.clubs.createClubWithSpec(null, body, clubUnauthorizedResponseSpec));

        Allure.step("Проверка: статус 401", () -> assertThat(response.statusCode()).isEqualTo(401));
    }

    @Test
    @DisplayName("Негативный: Создание клуба с пустым bookTitle (400 Bad Request)")
    public void createClubWithEmptyBookTitleTest() {
        CreateClubBodyModel body = new CreateClubBodyModel(
                EMPTY_STRING, CLUB_BOOK_AUTHORS, CLUB_PUBLICATION_YEAR,
                CLUB_DESCRIPTION, CLUB_TELEGRAM_LINK);

        var response = Allure.step("POST /clubs/ с пустым bookTitle", () ->
                api.clubs.createClubWithSpec(accessToken, body, clubBadRequestResponseSpec));

        Allure.step("Проверка: статус 400", () -> assertThat(response.statusCode()).isEqualTo(400));
    }

    @Test
    @DisplayName("Негативный: Получение несуществующего клуба (404 Not Found)")
    public void getNonExistentClubTest() {
        var response = Allure.step("GET /clubs/" + NON_EXISTENT_CLUB_ID + "/", () ->
                api.clubs.getClubByIdWithSpec(NON_EXISTENT_CLUB_ID, clubNotFoundResponseSpec));

        Allure.step("Проверка: статус 404", () -> assertThat(response.statusCode()).isEqualTo(404));
    }

    @Test
    @DisplayName("Негативный: Удаление несуществующего клуба (404 Not Found)")
    public void deleteNonExistentClubTest() {
        var response = Allure.step("DELETE /clubs/" + NON_EXISTENT_CLUB_ID + "/", () ->
                api.clubs.deleteClubWithSpec(accessToken, NON_EXISTENT_CLUB_ID, clubNotFoundResponseSpec));

        Allure.step("Проверка: статус 404", () -> assertThat(response.statusCode()).isEqualTo(404));
    }
}