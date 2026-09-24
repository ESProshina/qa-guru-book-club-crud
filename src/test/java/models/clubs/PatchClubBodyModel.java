package models.clubs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Тело запроса для частичного обновления клуба (PATCH /clubs/{id}/).
 * Все поля опциональны — можно передавать только те, что нужно изменить.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchClubBodyModel(
        @JsonProperty("bookTitle") String bookTitle,
        @JsonProperty("bookAuthors") String bookAuthors,
        @JsonProperty("publicationYear") Integer publicationYear,
        @JsonProperty("description") String description,
        @JsonProperty("telegramChatLink") String telegramChatLink
) {}