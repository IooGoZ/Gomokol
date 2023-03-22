#ifndef GAMEMANAGER_H
#define GAMEMANAGER_H

#include <stdlib.h>
#include <stdbool.h>

#include "game.h"
#include "client.h"

void gamemanager_connect(const char addr[], int port);

t_server get_server();

void subscribe_group(int group_id, void (*group) (int), int order);

t_game register_new_game(int game_id, int order);

bool server_player_register(int game_id, int player_id);

bool server_request_stroke(int game_id, int player_id);

bool server_send_stroke(int game_id, int player_id, int* position);

bool server_end_game(int game_id, int player_id);

bool server_new_group_game(int group_id, int game_id);

bool server_free_data(int game_id, int* data);

void wait_end_of_server(void);

#endif