package ru.practicum.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {
    private String app;
    private String uri;
    private Long hits;

    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        StatDto statDto = (StatDto) o;
        return this.app.equals(statDto.app)
                && this.uri.equals(statDto.uri)
                && this.hits.equals(statDto.hits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, uri, hits);
    }
}
