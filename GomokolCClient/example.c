#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>

#include "gamemanager.h"
#include "game.h"

#define BOARD_SIZE 15
#define ORDER 2
#define EMPTY -1

// Fonctions de lien avec l'API
int* get_random_position() {
    int *position = (int*) malloc(2 * sizeof(int));
    position[0] = (rand() % (BOARD_SIZE));
    position[1] = (rand() % (BOARD_SIZE)); 
    return position;
}

void add_stroke_to_board(int player, int* stroke) {
    
}

void group(int game_id) {
    t_game game = register_new_game(game_id, ORDER);

    register_new_board(game, add_stroke_to_board);
    
    register_new_player(game, get_random_position);
    register_new_player(game, get_random_position);
}

int main() {
    srand(time(NULL));

    char address[] = "192.168.160.1";

    gamemanager_connect(address, 8080);

    printf("Enter group id: ");
    int group_id = -1;
    scanf("%d", &group_id);

    subscribe_group(group_id, group, ORDER);


    wait_end_of_server();

    return EXIT_SUCCESS;
}