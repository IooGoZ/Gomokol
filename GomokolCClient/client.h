#ifndef CLIENT_H
#define CLIENT_H

#include <stdlib.h>
#include <stdbool.h>
#include "client.h"



typedef struct s_server {
    int socketfd;
    bool is_connected;
    char * addr;
    int port;
} * t_server;

t_server server_connect(const char addr[], int port);

void close_server(t_server serv);

void send_server_message(t_server serv, int* msg);

int read_server(t_server serv);

#endif