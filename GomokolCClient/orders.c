#include <stdlib.h>
#include <stdio.h>

#include "orders.h"

#define C_INIT_GAME 0
#define C_START_GAME 5
#define C_EMIT_STROKE 6
#define C_ANSWER_VALIDATION 7
#define C_REGISTER_PLAYER 8

int* client_init_game(int order) {
    int* msg = malloc(3 * sizeof(int));
    msg[0] = 3;
    msg[1] = C_INIT_GAME;
    msg[2] = order;
    return msg;
}

int* client_start_game(int game_id) {
    int* msg = malloc(3 * sizeof(int));
    msg[0] = 3;
    msg[1] = C_START_GAME;
    msg[2] = game_id;
    return msg;
}

int* client_emit_stroke(int game_id, int player_id, int order, int* stroke) {
    int* msg = malloc((size_t) (4 + order) * sizeof(int));
    msg[0] = 4 + order;
    msg[1] = C_EMIT_STROKE;
    msg[2] = game_id;
    msg[3] = player_id;
    for (int i = 0; i < order; i++) {
        msg[5 + i] = stroke[i];
    }
    return msg;
}

int* client_answer_validation(int game_id, int validation) {
    int* msg = malloc(4 * sizeof(int));
    msg[0] = 4;
    msg[1] = C_ANSWER_VALIDATION;
    msg[2] = game_id;
    msg[3] = validation;
    return msg;
}

int* client_register_player(int game_id) {
    int* msg = malloc(3 * sizeof(int));
    msg[0] = 3;
    msg[1] = C_REGISTER_PLAYER;
    msg[2] = game_id;
    return msg;
}

