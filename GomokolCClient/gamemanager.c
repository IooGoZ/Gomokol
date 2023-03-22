#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <pthread.h>

#include "orders.h"
#include "client.h"
#include "game.h"

#include "gamemanager.h"

#define TIMEOUT 3000
#define DEFAULT_VALUE -1
#define MAX_GAMES 1000
#define MAX_GROUPS 10


t_game games[MAX_GAMES];
t_group groups[MAX_GROUPS];


int game_count = 0;
int group_count = 0;

t_server server;

int game_id = DEFAULT_VALUE;


//Function with extern usage
void gamemanager_connect(const char addr[], int port) {
    server = server_connect(addr, port);
}

t_server get_server() {
    return server;
}

void subscribe_group(int group_id, void (*group) (int), int order) {
    send_server_message(server, client_subscribe_group(group_id));

    groups[group_count] = malloc(sizeof(struct s_group));
    groups[group_count]->id = group_id;
    groups[group_count]->group = group;
    groups[group_count]->order = order;

    group_count++;
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

t_group get_group_by_id(int group_id) {
    for (int i = 0; i < group_count; i++)
        if (groups[i]->id == group_id)
            return groups[i];
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

bool server_end_game(int game_id, int player_id) {
    t_game game = get_game_by_id(game_id);
    if (game == NULL)
        return false;
    
    server_send_end_game(game, player_id);
    return true;
}

void* new_group_game_thread(void* args) {
    t_group group = (t_group) args;
    
    group->group(group->current_game_id);

    return NULL;
}

bool server_new_group_game(int group_id, int game_id) {
    t_group group = get_group_by_id(group_id);
    if (group == NULL)
        return false;
    
    group->current_game_id = game_id;

    pthread_t thread;
    pthread_create(&thread, NULL, new_group_game_thread, group);

    return true;
}

bool server_free_data(int game_id, int* data) {
    t_game game = get_game_by_id(game_id);
    if (game == NULL)
        return false;
    
    server_send_free_data(game, data);
    return true;
}