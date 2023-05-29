package db;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
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
        firstUser = userStorage.createUser(firstUser);
        User user = userStorage.getUserById(firstUser.getId());

        assertThat(user)
                .extracting(User::getId, User::getName)
                .containsExactly(firstUser.getId(), "User1");
    }


    @Test
    public void testUpdateUser() {
        firstUser = userStorage.createUser(firstUser);

        User updateUser = new User();
        updateUser.setId(firstUser.getId());
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
    public void deleteUser() {
        firstUser = userStorage.createUser(firstUser);
        userStorage.delete(firstUser.getId());
        List<User> listUsers = userStorage.getAllUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void testCreateFilmAndGetFilmById() {
        firstFilm = filmStorage.create(firstFilm);

        Film film = filmStorage.getFilmById(firstFilm.getId());

        assertThat(film)
                .extracting(Film::getId, Film::getName)
                .containsExactly(firstFilm.getId(), "Фильм 1");
    }

    @Test
    public void testGetFilms() {
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);
        thirdFilm = filmStorage.create(thirdFilm);

        List<Film> listFilms = filmStorage.getFilms();

        assertThat(listFilms)
                .containsExactlyInAnyOrder(firstFilm, secondFilm, thirdFilm);
    }

    @Test
    public void testUpdateFilm() {
        firstFilm = filmStorage.create(firstFilm);

        Film updateFilm = new Film();
        updateFilm.setId(firstFilm.getId());
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
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);

        filmStorage.delete(firstFilm.getId());

        List<Film> listFilms = filmStorage.getFilms();
        assertThat(listFilms).hasSize(1);
        assertThat(listFilms.get(0))
                .hasFieldOrPropertyWithValue("name", "Фильм 2");
    }

    @Test
    public void testAddLike() {
        firstUser = userStorage.createUser(firstUser);
        firstFilm = filmStorage.create(firstFilm);

        filmService.addLike(firstFilm.getId(), firstUser.getId());

        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes())
                .hasSize(1)
                .contains(firstUser.getId());
    }

    @Test
    public void testDeleteLike() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), secondUser.getId());
        filmService.deleteLike(firstFilm.getId(), firstUser.getId());

        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes())
                .hasSize(1)
                .contains(secondUser.getId());
    }

    @Test
    public void testGetPopularFilms() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        firstFilm = filmStorage.create(firstFilm);
        filmService.addLike(firstFilm.getId(), firstUser.getId());

        secondFilm = filmStorage.create(secondFilm);
        filmService.addLike(secondFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), secondUser.getId());
        filmService.addLike(secondFilm.getId(), thirdUser.getId());

        thirdFilm = filmStorage.create(thirdFilm);
        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        filmService.addLike(thirdFilm.getId(), secondUser.getId());

        List<Film> listFilms = filmService.getPopular(5);

        assertThat(listFilms)
                .hasSize(3)
                .extracting(Film::getName)
                .containsExactly("Фильм 2", "Фильм 3", "Фильм 1");
    }

    @Test
    public void testAddFriend() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);

        userService.addFriend(firstUser.getId(), secondUser.getId());

        List<User> friends = userService.getFriends(firstUser.getId());

        assertThat(friends)
                .hasSize(1)
                .contains(secondUser);
    }

    @Test
    public void testDeleteFriend() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());

        userService.deleteFriend(firstUser.getId(), secondUser.getId());

        List<User> friends = userService.getFriends(firstUser.getId());

        assertThat(friends)
                .hasSize(1)
                .contains(thirdUser);
    }


    @Test
    public void testGetFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());

        List<User> friends = userService.getFriends(firstUser.getId());

        assertThat(friends)
                .hasSize(2)
                .contains(secondUser, thirdUser);
    }

    @Test
    public void testGetCommonFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());

        List<User> commonFriends = userService.getCommonFriends(firstUser.getId(), secondUser.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .contains(thirdUser);
    }
}