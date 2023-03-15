#ifndef ORDERS_H
#define ORDERS_H

#include <stdlib.h>

int* client_init_game(int order);

int* client_start_game(int game_id);

int* client_emit_stroke(int game_id, int player_id, int order, int* stroke);

int* client_answer_validation(int game_id, int validation);

int* client_register_player(int game_id);

#endif