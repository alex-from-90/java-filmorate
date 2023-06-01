package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getGenres() {
        log.info("Получен GET-запрос к эндпоинту: '/genres' на получение всех жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable @Valid Integer id) {
        log.info("Получен GET-запрос к эндпоинту: '/genres' на получение жанра с ID={}", id);
        return genreService.getGenreById(id);
    }
}