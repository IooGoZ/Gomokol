#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include "gamemanager.h"
#include "game.h"

#define BOARD_SIZE 15
#define EMPTY -1


int** board;


// Fonctions de lien avec l'API
int* get_position_black() {
    int x, y;
    best_move(BLACK, &x, &y);
    int* position = (int*) malloc(2 * sizeof(int));
    position[0] = x;
    position[1] = y;
    return position;
}

int* get_position_white() {
    int x, y;
    best_move(WHITE, &x, &y);
    int* position = (int*) malloc(2 * sizeof(int));
    position[0] = x;
    position[1] = y;
    return position;
}

void add_stroke_to_board(int player, int* stroke) {
    board[stroke[0]][stroke[1]] = player;
}

int main() {
    int i, j;

    // Initialiser le plateau
    board = (int**) malloc(BOARD_SIZE * sizeof(int*));
    for (i = 0; i < BOARD_SIZE; i++) {
        board[i] = (int*) malloc(BOARD_SIZE * sizeof(int));
        for (j = 0; j < BOARD_SIZE; j++) {
            board[i][j] = EMPTY;
        }
    }

    char address[] = "192.168.160.1";

    gamemanager_connect(address, 8080);
    t_game game = register_new_game(0, 2);

    register_new_board(game, add_stroke_to_board);
    
    register_new_player(game, get_position_black);
    register_new_player(game, get_position_white);


    wait_end_of_server();

    return EXIT_SUCCESS;
}