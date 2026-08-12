package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto hitDto) {
        log.info("Сервис: сохранение лога для app={}, uri={}", hitDto.getApp(), hitDto.getUri());
        statsRepository.save(statsMapper.toEndpointHit(hitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Сервис: запрос статистики с {} по {}, uris={}, unique={}", start, end, uris, unique);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала периода не может быть позже даты окончания");
        }

        if (unique) {
            return statsRepository.getStatsUnique(start, end, uris);
        } else {
            return statsRepository.getStats(start, end, uris);
        }
    }
}
