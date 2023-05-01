package ru.yandex.practicum.filmorate.controller.endpoints;

public final class ApiEndpointsUser {
    private ApiEndpointsUser() {
    }

    public static final String USERS = "/users";
    public static final String USERS_ID = "/users/{id}";
    public static final String USERS_ID_FRIENDS = "/users/{id}/friends";
    public static final String USERS_ID_FRIENDS_COMMON = "/users/{id}/friends/common/{otherId}";
    public static final String USERS_ID_FRIENDS_FRIENDID = "/users/{id}/friends/{friendId}";
}
