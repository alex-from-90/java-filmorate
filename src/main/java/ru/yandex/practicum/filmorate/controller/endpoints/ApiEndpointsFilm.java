package ru.yandex.practicum.filmorate.controller.endpoints;

public final class ApiEndpointsFilm {
    private ApiEndpointsFilm() {
    }

    public static final String FILMS = "/films";
    public static final String GET_FILMS = "";
    public static final String CREATE_FILM = "";
    public static final String UPDATE_FILM = "";
    public static final String GET_FILM_BY_ID = "/{id}";
    public static final String GET_POPULAR = "/popular";
    public static final String ADD_LIKE = "/{id}/like/{userId}";
    public static final String DELETE_LIKE = "/{id}/like/{userId}";
    public static final String DELETE_FILM = "/{id}";

}
