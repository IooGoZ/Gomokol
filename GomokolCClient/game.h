#ifndef GAME_H
#define GAME_H

#include <stdlib.h>
#include <stdbool.h>

#define MAX_BOARD_FUNCS 10
#define MAX_PLAYERS_PER_GAME 100
#define MAX_FREE_DATA_RECEIVER 10

typedef struct s_player {
    int id;
    int* (*get_position) (void);
} * t_player;

typedef struct s_game {
    int id;
    int order;

    void (*board[MAX_BOARD_FUNCS]) (int, int*);
    int board_count;

    t_player players[MAX_PLAYERS_PER_GAME];
    int player_count;

    void (*free_data_receiver[MAX_FREE_DATA_RECEIVER]) (int*);
    int free_data_receiver_count;

    int player_id;

} * t_game;

typedef struct s_group {
    int id;
    void (*group) (int);
    int order;
    int current_game_id;
} * t_group;



int get_game_id(t_game game);
int get_game_order(t_game game);
void register_new_board(t_game game, void (*fun) (int, int*));
t_player register_new_player(t_game game, int* (*get_position) (void));
void register_new_free_data_receiver(t_game game, void (*fun) (int*));

//DONT USE THESES FUNCTIONS
t_game create_game(int game_id, int order);
void server_set_player_id(t_game game, int player_id);
void server_request_player_stroke(t_game game, int player_id);
void server_send_game_stroke(t_game game, int player_id, int* position);
void server_send_end_game(t_game game, int player_id);
void server_send_free_data(t_game game, int* data);

#endif