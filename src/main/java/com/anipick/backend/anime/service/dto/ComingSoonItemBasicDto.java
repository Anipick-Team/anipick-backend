package com.anipick.backend.anime.service.dto;

import com.anipick.backend.anime.common.util.FormatConvert;
import com.anipick.backend.anime.domain.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ComingSoonItemBasicDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String startDate;
    private String format;
    private Boolean isAdult;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    public ComingSoonItemBasicDto typeToReleaseDate() {
        if (this.startDate == null || this.startDate.isBlank()) {
            this.startDate = "미정";
            return this;
        }

        List<String> tvFormats = FormatConvert.toConvert("TVA");

        if (tvFormats.contains(this.format)) {
            // YY년 Q분기
            String startDateStr = this.startDate;
            LocalDate date = LocalDate.parse(startDateStr);

            Season season = Season.containsSeason(date);
            String seasonName = season.getName();

            Integer year = date.getYear();
            String yearString = year.toString().substring(2, 4);

            this.startDate = yearString + "년 " + seasonName;
        } else {
            //YYYY. MM. DD
            LocalDate date = LocalDate.parse(this.startDate);
            this.startDate = date.format(FORMATTER);
        }
        return this;
    }
}
