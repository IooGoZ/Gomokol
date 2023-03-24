#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>

#include "gamemanager.h"
#include "game.h"

#define BOARD_SIZE 15
#define ORDER 2
#define EMPTY -1

//Plateau de jeu
int board[BOARD_SIZE][BOARD_SIZE];

//Fonction appelée lors de la demande d'un coup
int* get_random_position() {

    //On alloue la mémoire pour le coup
    int *position = (int*) malloc(ORDER * sizeof(int));

    //On génère un coup aléatoire (pour l'exemple)
    do {
        position[0] = (rand() % (BOARD_SIZE));
        position[1] = (rand() % (BOARD_SIZE));  
    } while (board[position[0]][position[1]] != EMPTY);
    
    //On retourne le coup
    return position;
}

//Fonction appelée lors de la réception d'un coup joué
void add_stroke_to_board(int player, int* stroke) {

    //On récupère les coordonnées du coup joué
    int x = stroke[0];
    int y = stroke[1];

    //On les place sur le plateau
    board[x][y] = player;
}

//Fonction appelée lors de la création d'une partie dans le groupe inscrit
void group(int game_id) {

    //On initialise le plateau de jeu
    for (int i = 0; i < BOARD_SIZE; i++)
        for (int j = 0; j < BOARD_SIZE; j++)
            board[i][j] = EMPTY;

    //On enregistre la partie
    t_game game = register_new_game(game_id, ORDER);

    //On enregistre les fonctions de réception des coups joués
    register_new_board(game, add_stroke_to_board);
    
    //On enregistre les joueurs
    register_new_player(game, get_random_position);
    register_new_player(game, get_random_position);
}



int main() {
    
    //On définit l'adresse du serveur
    char address[] = "192.168.160.1";

    //On se connecte au serveur
    gamemanager_connect(address, 8080);

    //On initialise le générateur de nombres aléatoires
    srand(time(NULL));

    //Récupération de l'id du groupe
    int group_id = -1;
    printf("Entrez l'id du groupe : ");
    scanf("%d", &group_id);

    //On s'inscrit au groupe
    subscribe_group(group_id, group, ORDER);

    //On attend la fin du serveur
    wait_end_of_server();

    return EXIT_SUCCESS;
}