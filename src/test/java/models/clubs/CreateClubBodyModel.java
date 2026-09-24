package models.clubs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Тело запроса для создания клуба (POST /clubs/).
 * Обязательные поля: bookTitle, bookAuthors, publicationYear, description, telegramChatLink.
 */
public record CreateClubBodyModel(
        @JsonProperty("bookTitle") String bookTitle,
        @JsonProperty("bookAuthors") String bookAuthors,
        @JsonProperty("publicationYear") Integer publicationYear,
        @JsonProperty("description") String description,
        @JsonProperty("telegramChatLink") String telegramChatLink
) {}
