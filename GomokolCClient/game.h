#ifndef GAME_H
#define GAME_H

#include <stdlib.h>
#include <stdbool.h>

typedef struct s_player {
    int id;
    int* (*get_position) (void);
} * t_player;

typedef struct s_game {
    int id;
    int order;

    void (*board[100]) (int, int*);
    int board_count;

    t_player players[100];
    int player_count;

    int player_id;

} * t_game;




int get_game_id(t_game game);
int get_game_order(t_game game);
void register_new_board(t_game game, void (*fun) (int, int*));
t_player register_new_player(t_game game, int* (*get_position) (void));

//DONT USE THESES FUNCTIONS
t_game create_game(int game_id, int order);
void server_set_player_id(t_game game, int player_id);
void server_request_player_stroke(t_game game, int player_id);
void server_send_game_stroke(t_game game, int player_id, int* position);

#endif