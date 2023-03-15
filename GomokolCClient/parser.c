#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include "client.h"
#include "parser.h"
#include "gamemanager.h"

#define S_REQUEST_STROKE 1
#define S_SEND_STROKE 2
#define S_REQUEST_VALIDATION 3
#define S_GAME_CREATED 4
#define S_PLAYER_REGISTERED 9
#define S_ERROR_REQUEST 10

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

bool parser_server_request_validation(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);
    int* stroke = read_int_array(serv);

    printf("Call to server_request_validation : Game %d, player %d, stroke %d", game_id, player_id, stroke[0]);

    return true;
}

bool parser_server_game_created(t_server serv) {
    int game_id = read_server(serv);

    printf("Call to server_game_created : Game %d", game_id);

    return true;
}

bool parser_server_player_registered(t_server serv) {
    int game_id = read_server(serv);
    int player_id = read_server(serv);

    return server_player_register(game_id, player_id);
}

bool parser_server_error_request(t_server serv) {
    int order = read_server(serv);

    printf("Error : Order %d is not valid.\n", order);

    return true;
}

bool parse(t_server serv, int order) {
    switch (order) {
        case S_REQUEST_STROKE:
            return parser_server_request_stroke(serv);
        case S_SEND_STROKE:
            return parser_server_send_stroke(serv);
        case S_REQUEST_VALIDATION:
            return parser_server_request_validation(serv);
        case S_GAME_CREATED:
            return parser_server_game_created(serv);
        case S_PLAYER_REGISTERED:
            return parser_server_player_registered(serv);
        case S_ERROR_REQUEST:
            return parser_server_error_request(serv);
        default:
            printf("Error : Unknown order.\n");
            return false;
    }
    return true;
}