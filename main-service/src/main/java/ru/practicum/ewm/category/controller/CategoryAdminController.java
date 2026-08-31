package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Администратор: запрос на создание категории name={}", newCategoryDto.getName());
        CategoryDto createdCategory = categoryService.createCategory(newCategoryDto);
        log.info("Администратор: категория id={}, name={} успешно создана",
                createdCategory.getId(), createdCategory.getName());
        return createdCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Администратор: запрос на удаление категории id={}", catId);
        categoryService.deleteCategory(catId);
        log.info("Администратор: категория id={} успешно удалена", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Администратор: запрос на изменение категории id={}", catId);
        CategoryDto updatedCategory = categoryService.updateCategory(catId, categoryDto);
        log.info("Администратор: категория id={} успешно изменена. Новое имя: '{}'",
                updatedCategory.getId(), updatedCategory.getName());
        return updatedCategory;
    }

}
