package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private final RestClient restClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${STATS_SERVER_URL:http://localhost:9090}") String serverUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    // POST /hit — Сохранение информации о просмотре страницы.
    public void saveHit(EndpointHitDto hitDto) {
        log.info("Клиент: отправка POST-запроса /hit для приложения {}", hitDto.getApp());

        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    // GET /stats — Получение агрегированной статистики по просмотрам.
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Клиент: отправка GET-запроса /stats (start={}, end={}, uris={}, unique={})",
                start, end, uris, unique);

        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        String encodedStart = URLEncoder.encode(startStr, StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(endStr, StandardCharsets.UTF_8);

        StringBuilder uriBuilder = new StringBuilder("/stats")
                .append("?start=").append(encodedStart)
                .append("&end=").append(encodedEnd)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        return restClient.get()
                .uri(uriBuilder.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {});
    }
}
