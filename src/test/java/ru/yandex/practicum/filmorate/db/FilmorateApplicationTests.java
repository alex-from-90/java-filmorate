package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User();
        firstUser.setName("User1");
        firstUser.setLogin("First");
        firstUser.setEmail("one@yandex.ru");
        firstUser.setBirthday(LocalDate.of(2000, 2, 11));

        secondUser = new User();
        secondUser.setName("User2");
        secondUser.setLogin("Second");
        secondUser.setEmail("two@yandex.ru");
        secondUser.setBirthday(LocalDate.of(1980, 3, 5));

        thirdUser = new User();
        thirdUser.setName("MisterThird");
        thirdUser.setLogin("Third");
        thirdUser.setEmail("3@ya.ru");
        thirdUser.setBirthday(LocalDate.of(1980, 12, 25));

        firstFilm = new Film();
        firstFilm.setName("Фильм 1");
        firstFilm.setDescription("Описание фильма 1");
        firstFilm.setReleaseDate(LocalDate.of(1961, 10, 5));
        firstFilm.setDuration(114);
        firstFilm.setMpa(new Mpa(1, "G"));
        firstFilm.setLikes(new HashSet<>());
        firstFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"),
                new Genre(1, "Комедия"))));

        secondFilm = new Film();
        secondFilm.setName("Фильм 2");
        secondFilm.setDescription("Описание фильма 2");
        secondFilm.setReleaseDate(LocalDate.of(2009, 12, 10));
        secondFilm.setDuration(162);
        secondFilm.setMpa(new Mpa(3, "PG-13"));
        secondFilm.setLikes(new HashSet<>());
        secondFilm.setGenres(new HashSet<>(Collections.singletonList(new Genre(6, "Боевик"))));

        thirdFilm = new Film();
        thirdFilm.setName("Фильм 3");
        thirdFilm.setDescription("Описание фильма 3");
        thirdFilm.setReleaseDate(LocalDate.of(1975, 11, 19));
        thirdFilm.setDuration(133);
        thirdFilm.setMpa(new Mpa(4, "R"));
        thirdFilm.setLikes(new HashSet<>());
        thirdFilm.setGenres(new HashSet<>(Collections.singletonList(new Genre(2, "Драма"))));
    }

    @Test
    public void testCreateUserAndGetUserById() {
        User createdUser = userStorage.createUser(firstUser);
        User retrievedUser = userStorage.getUserById(createdUser.getId());

        assertThat(retrievedUser)
                .extracting(User::getId, User::getName, User::getLogin, User::getEmail, User::getBirthday)
                .containsExactly(
                        createdUser.getId(),
                        createdUser.getName(),
                        createdUser.getLogin(),
                        createdUser.getEmail(),
                        createdUser.getBirthday()
                );
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userStorage.createUser(firstUser);

        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setName("UpdateUser1");
        updateUser.setLogin("First");
        updateUser.setEmail("one@yandex.ru");
        updateUser.setBirthday(LocalDate.of(2000, 2, 11));

        User updatedUser = userStorage.updateUser(updateUser);

        assertThat(updatedUser)
                .extracting(User::getName)
                .isEqualTo("UpdateUser1");
    }

    @Test
    public void testDeleteUser() {
        User createdUser = userStorage.createUser(firstUser);
        userStorage.delete(createdUser.getId());
        List<User> listUsers = userStorage.getAllUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void testCreateFilmAndGetFilmById() {
        Film createdFilm = filmStorage.create(firstFilm);

        Film retrievedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(retrievedFilm)
                .extracting(Film::getId, Film::getName)
                .containsExactly(createdFilm.getId(), "Фильм 1");
    }

    @Test
    public void testGetFilms() {
        Film createdFirstFilm = filmStorage.create(firstFilm);
        Film createdSecondFilm = filmStorage.create(secondFilm);
        Film createdThirdFilm = filmStorage.create(thirdFilm);

        List<Film> listFilms = filmStorage.getFilms();

        assertThat(listFilms)
                .containsExactlyInAnyOrder(createdFirstFilm, createdSecondFilm, createdThirdFilm);
    }

    @Test
    public void testUpdateFilm() {
        Film createdFilm = filmStorage.create(firstFilm);

        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setName("UpdateName");
        updateFilm.setDescription("UpdateDescription");
        updateFilm.setReleaseDate(LocalDate.of(1975, 11, 19));
        updateFilm.setDuration(133);
        updateFilm.setMpa(new Mpa(1, "G"));

        Film updatedFilm = filmStorage.update(updateFilm);

        assertThat(updatedFilm)
                .hasFieldOrPropertyWithValue("name", "UpdateName")
                .hasFieldOrPropertyWithValue("description", "UpdateDescription");
    }

    @Test
    public void testDeleteFilm() {
        Film createdFirstFilm = filmStorage.create(firstFilm);
        filmStorage.delete(createdFirstFilm.getId());
        List<Film> listFilms = filmStorage.getFilms();
        assertThat(listFilms).hasSize(0);
    }

    @Test
    public void testAddLike() {
        User createdUser = userStorage.createUser(firstUser);
        Film createdFilm = filmStorage.create(firstFilm);

        filmService.addLike(createdFilm.getId(), createdUser.getId());

        Film updatedFilm = filmStorage.getFilmById(createdFilm.getId());
        assertThat(updatedFilm.getLikes())
                .hasSize(1)
                .contains(createdUser.getId());
    }

    @Test
    public void testDeleteLike() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);
        Film createdFilm = filmStorage.create(firstFilm);

        filmService.addLike(createdFilm.getId(), createdFirstUser.getId());
        filmService.addLike(createdFilm.getId(), createdSecondUser.getId());
        filmService.deleteLike(createdFilm.getId(), createdFirstUser.getId());

        Film updatedFilm = filmStorage.getFilmById(createdFilm.getId());
        assertThat(updatedFilm.getLikes())
                .hasSize(1)
                .contains(createdSecondUser.getId());
    }

    @Test
    public void testGetPopularFilms() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);
        User createdThirdUser = userStorage.createUser(thirdUser);

        Film createdFirstFilm = filmStorage.create(firstFilm);
        filmService.addLike(createdFirstFilm.getId(), createdFirstUser.getId());

        Film createdSecondFilm = filmStorage.create(secondFilm);
        filmService.addLike(createdSecondFilm.getId(), createdFirstUser.getId());
        filmService.addLike(createdSecondFilm.getId(), createdSecondUser.getId());
        filmService.addLike(createdSecondFilm.getId(), createdThirdUser.getId());

        Film createdThirdFilm = filmStorage.create(thirdFilm);
        filmService.addLike(createdThirdFilm.getId(), createdFirstUser.getId());
        filmService.addLike(createdThirdFilm.getId(), createdSecondUser.getId());

        List<Film> listFilms = filmService.getPopular(5);

        assertThat(listFilms)
                .hasSize(3)
                .extracting(Film::getName)
                .containsExactly("Фильм 2", "Фильм 3", "Фильм 1");
    }

    @Test
    public void testAddFriend() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);

        userService.addFriend(createdFirstUser.getId(), createdSecondUser.getId());

        List<User> friends = userService.getFriends(createdFirstUser.getId());

        assertThat(friends)
                .hasSize(1)
                .anyMatch(user -> user.getId().equals(createdSecondUser.getId()));
    }

    @Test
    public void testDeleteFriend() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);
        User createdThirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        userService.addFriend(createdFirstUser.getId(), createdThirdUser.getId());

        userService.deleteFriend(createdFirstUser.getId(), createdSecondUser.getId());

        List<User> friends = userService.getFriends(createdFirstUser.getId());

        assertThat(friends)
                .hasSize(1)
                .anyMatch(user -> user.getId().equals(createdThirdUser.getId()));
    }


    @Test
    public void testGetFriends() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);
        User createdThirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        userService.addFriend(createdFirstUser.getId(), createdThirdUser.getId());

        List<User> friends = userService.getFriends(createdFirstUser.getId());

        assertThat(friends)
                .hasSize(2)
                .anyMatch(user -> user.getId().equals(createdSecondUser.getId()))
                .anyMatch(user -> user.getId().equals(createdThirdUser.getId()));
    }

    @Test
    public void testGetCommonFriends() {
        User createdFirstUser = userStorage.createUser(firstUser);
        User createdSecondUser = userStorage.createUser(secondUser);
        User createdThirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        userService.addFriend(createdFirstUser.getId(), createdThirdUser.getId());
        userService.addFriend(createdSecondUser.getId(), createdFirstUser.getId());
        userService.addFriend(createdSecondUser.getId(), createdThirdUser.getId());

        List<User> commonFriends = userService.getCommonFriends(createdFirstUser.getId(), createdSecondUser.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .satisfies(userList -> assertThat(userList.get(0).getId()).isEqualTo(createdThirdUser.getId()));
    }
}