#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>

#include "game.h"
#include "gamemanager.h"
#include "client.h"
#include "orders.h"

#define TIMEOUT 3000
#define DEFAULT_VALUE -1


t_game create_game(int id, int order) {
    t_game game = malloc(sizeof(struct s_game));
    game->id = id;
    game->order = order;
    game->player_count = 0;
    game->board_count = 0;
    game->free_data_receiver_count = 0;
    game->player_id = DEFAULT_VALUE;
    return game;
}

int get_game_id(t_game game) {
    return game->id;
}

int get_game_order(t_game game) {
    return game->order;
}

void register_new_board(t_game game, void (*fun) (int, int*)) {
    game->board[game->board_count] = fun;
    game->board_count++;
}

void register_new_free_data_receiver(t_game game, void (*fun) (int*)) {
    game->free_data_receiver[game->free_data_receiver_count] = fun;
    game->free_data_receiver_count++;
}

// Pas de get_player_at_position

t_player register_new_player(t_game game, int* (*get_position) (void)) {

    game->player_id = DEFAULT_VALUE;

    send_server_message(get_server(), client_register_player(game->id));

    time_t start = time(NULL);
    while (game->player_id == DEFAULT_VALUE)
        if (time(NULL) - start > TIMEOUT)
            return NULL;

    t_player player = malloc(sizeof(struct s_player));
    player->id = game->player_id;
    player->get_position = get_position;

    game->player_id = DEFAULT_VALUE;

    game->players[game->player_count] = player;
    game->player_count++;

    return player;
}

t_player get_player_by_id(t_game game, int player_id) {
    for (int i = 0; i < game->player_count; i++)
        if (game->players[i]->id == player_id)
            return game->players[i];
    return NULL;
}

void server_set_player_id(t_game game, int player_id) {
    game->player_id = player_id;
}

void server_request_player_stroke(t_game game, int player_id) {
    t_player player = get_player_by_id(game, player_id);

    int* position = player->get_position();

    send_server_message(get_server(), client_emit_stroke(game->id, player_id, game->order, position));

    free(position);
}

void server_send_game_stroke(t_game game, int player_id, int* position) {
    for (int i = 0; i < game->board_count; i++)
        game->board[i](player_id, position);

    free(position);
}

void server_send_end_game(t_game game, int player_id) {
    if (player_id == -2) {
        printf("La partie %d est nulle.", game->id);
    } else if (player_id == -1) {
        printf("La partie %d s'est terminée sans gagnant.", game->id);
    } else {
        printf("La partie %d est terminée : le joueur %d a gagné.", game->id, player_id);
    }
}

void server_send_free_data(t_game game, int* data) {
    for (int i = 0; i < game->free_data_receiver_count; i++)
        game->free_data_receiver[i](data);

    free(data);
}


