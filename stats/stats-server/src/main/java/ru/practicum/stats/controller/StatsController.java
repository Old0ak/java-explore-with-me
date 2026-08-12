package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Сохранение информации о том, что к эндпоинту был запрос
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Получен запрос на сохранение статистики: {}", hitDto);
        statsService.saveHit(hitDto);
        log.info("Статистика успешно сохранена");
    }

    // Получение статистики по посещениям
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получен запрос на получение статистики: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        LocalDateTime startTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);

        List<ViewStatsDto> viewStats = statsService.getStats(startTime, endTime, uris, unique);
        log.info("Отправка ответа клиенту, найдено записей: {}", viewStats.size());
        return viewStats;
    }

}
