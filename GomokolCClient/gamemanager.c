#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>

#include "orders.h"
#include "client.h"
#include "game.h"

#include "gamemanager.h"

#define TIMEOUT 3000
#define DEFAULT_VALUE -1
#define MAX_GAMES 100


t_game games[MAX_GAMES];
int game_count = 0;

t_server server;

int game_id = DEFAULT_VALUE;


//Function with extern usage
void gamemanager_connect(const char addr[], int port) {
    server = server_connect(addr, port);
}

t_server get_server() {
    return server;
}

t_game register_new_game(int game_id, int order) {
    t_game game = create_game(game_id, order);

    games[game_count] = game;
    game_count++;

    return game;
}

void wait_end_of_server() {
    while(server->is_connected);
}

//Function 

t_game get_game_by_id(int game_id) {
    for (int i = 0; i < game_count; i++)
        if (games[i]->id == game_id)
            return games[i];
    return NULL;
}

bool server_player_register(int game_id, int player_id) {
    t_game game = get_game_by_id(game_id);
    if (game == NULL)
        return false;
    server_set_player_id(game, player_id);
    return true;
}

bool server_request_stroke(int game_id, int player_id) {
    t_game game = get_game_by_id(game_id);
    if (game == NULL)
        return false;
    
    server_request_player_stroke(game, player_id);
    return true;
}

bool server_send_stroke(int game_id, int player_id, int* position) {
    t_game game = get_game_by_id(game_id);
    if (game == NULL)
        return false;
    
    server_send_game_stroke(game, player_id, position);
    return true;
}