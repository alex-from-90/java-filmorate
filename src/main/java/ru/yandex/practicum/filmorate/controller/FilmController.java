package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.model.DirectorSortBy;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма с ID={}",
                film.getId());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма с ID={}",
                film.getId());
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильма с ID={}", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Long id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", id);
        return filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на добавление лайка фильму с ID={}", id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление лайка у фильма с ID={}",
                id);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable int directorId, @RequestParam DirectorSortBy sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> filmsSearch(@RequestParam String query,
                                  @RequestParam(defaultValue = "") String by) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на поиск по: " + query + " " + by);
        return filmService.filmSearch(query, by);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(defaultValue = "-1") Integer genreId,
                                      @RequestParam(defaultValue = "-1") Integer year) {
        log.info("Запрос лучших фильмов, count = {}, genreId = {}, year = {}", count, genreId,
                year);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> commonFilms(@RequestParam Long userId, Long friendId) {
        log.info("Запрос общих фильмов пользователей с id {} и id {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}