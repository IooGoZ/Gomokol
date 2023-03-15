#ifndef GAMEMANAGER_H
#define GAMEMANAGER_H

#include <stdlib.h>
#include <stdbool.h>

#include "game.h"
#include "client.h"

void connect(char * ip, int port);

t_server get_server();

t_game register_new_game(int game_id, int order);

bool server_player_register(int game_id, int player_id);

bool server_request_stroke(int game_id, int player_id);

bool server_send_stroke(int game_id, int player_id, int* position);

#endif