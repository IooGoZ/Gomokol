#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include "client.h"
#include "parser.h"
#include "gamemanager.h"

#define S_REQUEST_STROKE 1
#define S_SEND_STROKE 2
#define S_PLAYER_REGISTERED 9
#define S_ERROR_REQUEST 10
#define S_END_GAME 11
#define S_NEW_GROUP_GAME 13
#define S_FREE_DATA 17

int* read_int_array(t_server serv) {
    int size = read_server(serv);
    int* array = malloc((size_t) (size) * sizeof(int)); 

    for (int i = 0; i < size; i++) {
        array[i] = read_server(serv);
    }
    return array;
}

bool parser_server_request_stroke(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);

    return server_request_stroke(game_id, player_id);
}

bool parser_server_send_stroke(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);
    int* stroke = read_int_array(serv);

    return server_send_stroke(game_id, player_id, stroke);
}

bool parser_server_player_registered(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);

    return server_player_register(game_id, player_id);
}

bool parser_server_error_request(t_server serv) {
    int order = read_server(serv);

    printf("Error : Order %d is not valid.\n", order);

    return false;
}

bool parser_end_of_game(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);

    return server_end_game(game_id, player_id);
}

bool parser_new_group_game(t_server serv) {
    int group_id = read_server(serv);
    int game_id = read_server(serv);

    return server_new_group_game(group_id, game_id);
}

bool parser_free_data(t_server serv) {
    int game_id = read_server(serv);
    int* data = read_int_array(serv);

    return server_free_data(game_id, data);
}

bool parse(t_server serv, int order) {

    printf("Parsing order %d.\n", order);

    switch (order) {
        case S_REQUEST_STROKE:
            return parser_server_request_stroke(serv);
        case S_SEND_STROKE:
            return parser_server_send_stroke(serv);
        case S_PLAYER_REGISTERED:
            return parser_server_player_registered(serv);
        case S_ERROR_REQUEST:
            return parser_server_error_request(serv);
        case S_END_GAME:
            return parser_end_of_game(serv);
        case S_NEW_GROUP_GAME:
            return parser_new_group_game(serv);
        case S_FREE_DATA:
            return parser_free_data(serv);
        default:
            printf("Error : Unknown order.\n");
            return false;
    }
    return true;
}