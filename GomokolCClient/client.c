#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>

#include "client.h"

void init_serv_addr(struct sockaddr_in * servaddr, char* addr, uint16_t port) {
	(*servaddr).sin_family = AF_INET;
	(*servaddr).sin_addr.s_addr = inet_addr(addr);
	(*servaddr).sin_port = htons(port);
}

int read_server(t_server serv) {
	unsigned int buffer = 0;
	
	read(serv->socketfd, &buffer, sizeof(buffer));

	return (int) ntohl(buffer);
}

static void* reading_thread(void* args) {
	t_server serv = (t_server) args;

	while (serv->is_connected) {
		int msg = read_server(serv);

		printf("Message recu : %d\n", msg);
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

t_server server_connect(char * addr, int port) {
	t_server serv = malloc(sizeof(struct s_server));
	
	struct sockaddr_in servaddr;

	serv->socketfd = socket(AF_INET, SOCK_STREAM, 0);
	
	if (serv->socketfd == -1) {
		printf("Error : Echec de la creation du socket.\n");
		exit(0);
	}
	
	bzero(&servaddr, sizeof(servaddr));

	init_serv_addr(&servaddr, addr, (uint16_t) port);

	if (connect(serv->socketfd, (struct sockaddr *) &servaddr, sizeof(servaddr))
		!= 0) {
		printf("Error : Echec de la connexion au serveur.\n");
		exit(0);
	} else
		serv->is_connected = true;

	start_server_reading(serv);

	return serv;
}

void close_server(t_server serv) {
	serv->is_connected = false;
	close(serv->socketfd);
	free(serv);
}

void send_server(t_server serv, unsigned int msg) {
	unsigned int buffer = htonl(msg);
	if (serv->is_connected)
		write(serv->socketfd, &buffer, sizeof(int));
}

void send_server_message(t_server serv, int* msg) {
	int len = msg[0];
	for (int i = 0; i < len; i++) {
		send_server(serv, (unsigned int) msg[i]);
	}
}

int main() {
	t_server serv = server_connect("192.168.160.1", 8080);

	send_server(serv, 0);
	send_server(serv, 2);

	while (1)
	{
		/* code */
	}
	

	close_server(serv);

	return EXIT_SUCCESS;
}