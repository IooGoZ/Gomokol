#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#include <sys/time.h>

#include "client.h"
#include "parser.h"

int read_server(t_server serv) {
	unsigned int entier;
    ssize_t n;
    do {
		n = recv(serv->socketfd, &entier, sizeof(entier), 0);
		if (n < 0) {
			perror("Erreur de lecture sur le socket");
			exit(1);
		}
	} while (n <= 0);
	entier = ntohl(entier); // conversion du format réseau
	return (int) entier;
    
}

static void* reading_thread(void* args) {

	t_server serv = (t_server) args;

	struct sockaddr_in servaddr;

	bzero(&servaddr, sizeof(struct sockaddr_in));

	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons((uint16_t) serv->port);

 	if (inet_pton(AF_INET, serv->addr, &servaddr.sin_addr.s_addr) <= 0) {
        printf("Adresse IP invalide ou non prise en charge\n");
        exit(0);
    }

	

	if (connect(serv->socketfd, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0) {
		printf("Error : Echec de la connexion au serveur.\n");
		exit(0);
	} else
		serv->is_connected = true;

	printf("Connexion etablie.\n");

	while (serv->is_connected) {
		int msg = read_server(serv);
		parse(serv, msg);
	}

	return (void*) NULL;
}

void start_server_reading(t_server serv) {
	pthread_t thread;

	int ret = pthread_create(&(thread), NULL, reading_thread, serv);
	if (ret != 0) {
		printf("Error : Echec de la creation du thread de lecture.\n");
		exit(0);
	}
}

t_server server_connect(const char addr[], int port) {

	t_server serv = malloc(sizeof(struct s_server));

	serv->socketfd = socket(AF_INET, SOCK_STREAM, 0);
	serv->addr = malloc(sizeof(char) * (strlen(addr) + 1));
	strcpy(serv->addr, addr);
	serv->port = port;
	serv->is_connected = false;

	if (serv->socketfd < 0) {
		printf("Error : Echec de la creation du socket.\n");
		exit(0);
	}

	 // désactiver le timeout pour l'envoi
    struct timeval time;
    time.tv_sec = 0;
    time.tv_usec = 0;
    setsockopt(serv->socketfd , SOL_SOCKET, SO_SNDTIMEO, &time, sizeof(time));

    // désactiver le timeout pour la réception
    setsockopt(serv->socketfd , SOL_SOCKET, SO_RCVTIMEO, &time, sizeof(time));

	printf("Connexion au serveur...\n");

	start_server_reading(serv);

	while (!serv->is_connected);

	return serv;
}

void close_server(t_server serv) {
	serv->is_connected = false;
	close(serv->socketfd);
	free(serv);
}

void send_server(t_server serv, unsigned int msg) {
	unsigned int buffer = htonl(msg);
	if (serv->is_connected) {
		ssize_t bytes_sent = send(serv->socketfd, &buffer, sizeof(buffer), 0);

		if (bytes_sent == -1) {
			perror("send");
			exit(EXIT_FAILURE);
		} else if (bytes_sent == 0) {
			fprintf(stderr, "La connexion a été fermée par le côté distant\n");
			exit(EXIT_FAILURE);
		} else if (bytes_sent != sizeof(buffer)) {
			fprintf(stderr, "Erreur d'envoi : seuls %ld octets ont été envoyés au lieu de %ld\n",
					bytes_sent, sizeof(buffer));
			exit(EXIT_FAILURE);
		}
	} else fprintf(stderr, "Error : Le serveur n'est pas connecte.\n");
		
}

void send_server_message(t_server serv, int* msg) {
	int len = msg[0];
	for (int i = 1; i < len; i++) {
		send_server(serv, (unsigned int) msg[i]);
	}
	free(msg);
}