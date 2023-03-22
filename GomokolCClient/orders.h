#ifndef ORDERS_H
#define ORDERS_H

#include <stdlib.h>

int* client_emit_stroke(int game_id, int player_id, int order, int* stroke);

int* client_register_player(int game_id);

int* client_subscribe_group(int group_id);

int* client_free_data(int length, int* data);

#endif