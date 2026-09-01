package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Публичный API: запрос списка категорий. from={}, size={}", from, size);
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        log.info("Публичный API: успешно возвращено {} категорий", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Публичный API: запрос категории с id={}", catId);
        CategoryDto category = categoryService.getCategoryById(catId);
        log.info("Публичный API: успешно найдена категория: '{}'", category.getName());
        return category;
    }
}
