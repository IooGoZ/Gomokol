#include <stdlib.h>
#include <stdio.h>

#include "orders.h"

#define C_EMIT_STROKE 6
#define C_REGISTER_PLAYER 8
#define C_SUBSCRIBE_GROUP 12
#define C_FREE_DATA 16

int* client_emit_stroke(int game_id, int player_id, int order, int* stroke) {
    int* msg = malloc((size_t) (5 + order) * sizeof(int));
    msg[0] = 5 + order;
    msg[1] = C_EMIT_STROKE;
    msg[2] = game_id;
    msg[3] = player_id;
    msg[4] = order;
    for (int i = 0; i < order; i++) {
        msg[5 + i] = stroke[i];
    }
    return msg;
}

int* client_register_player(int game_id) {
    int* msg = malloc(3 * sizeof(int));
    msg[0] = 3;
    msg[1] = C_REGISTER_PLAYER;
    msg[2] = game_id;
    return msg;
}

int* client_subscribe_group(int group_id) {
    int* msg = malloc(3 * sizeof(int));
    msg[0] = 3;
    msg[1] = C_SUBSCRIBE_GROUP;
    msg[2] = group_id;
    return msg;
}

int* client_free_data(int length, int* data) {
    int* msg = malloc((size_t) (2 + length) * sizeof(int));
    msg[0] = 2 + length;
    msg[1] = C_FREE_DATA;
    for (int i = 0; i < length; i++) {
        msg[2 + i] = data[i];
    }
    return msg;
}