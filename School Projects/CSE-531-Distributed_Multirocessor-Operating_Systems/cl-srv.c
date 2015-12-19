// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#include <string.h>
#include "msgs.h"

//  Predefined command metadata
#define ADD 0
#define MODIFY 1
#define DELETE 2
#define PRINT 3

//  Meta data for decoding message
#define SRC_PORT_IDX 0
#define REQ_OP_IDX 1
#define TABLE_ROW_IDX 2

//  Can be modified as per wish
#define TABLE_ROWS 16
#define MAX_STRING_LEN 256
#define BUFFER_SIZE 10
#define NUM_OF_SERVERS 1
#define NUM_OF_WRITER_CLIENTS 2
#define NUM_OF_READER_CLIENTS 1
#define PORT_DEPTH 10
#define DELAY 1
#define NUM_PORTS 100
#define MSG_SIZE 10
//  disbale this if you don't want to observe each step
#define DEBUG 0

Port_t ports[NUM_PORTS];
int serverPort = 0;
int nextClientPort = NUM_OF_SERVERS;
Semaphore_t* table;

//  Commented since using only one server...Can be used to get random server if there are multiple
/*
int getNextServerPort() {
    int currPort = next_server_port;
    next_server_port++;
    return (currPort);
}
int getNextClientPort() {
    int currPort = next_client_port;
    next_client_port++;
    return (currPort);
}
*/

int randint(int max) {
    int randomNumber = (int)rand() % max;
    return randomNumber;
}

//  Retrieve string message from given port
void getStrMsg(char *str, int port) {
	Message_t msg;
	int i = 0, j = 0;
	do {
		msg = Receive(&ports[port]);
		for (j = 0; j < BUFFER_SIZE; j++, i++) {
			str[i] = msg.data[j];
			if (!msg.data[j]) {
				break;
			}
		}
	} while (msg.data[j]);
}

//  Send string message to given port
void sendStrMsg(char *str, int port) {
	Message_t msg;
	int i = 0, j = 0;
	do {
		for (j = 0; j < BUFFER_SIZE; j++, i++) {
			msg.data[j] = str[i];
			if (!str[i]) {
				break;
			}
		}
		Send(&ports[port], msg);
	} while (str[i]);
}

//  Server method starts the server listens to clients
void server() {
	char serverTable[TABLE_ROWS][MAX_STRING_LEN];
    int p;
    for(p = 0; p < TABLE_ROWS; p++) {
        strcpy(serverTable[p], "");
    }
    //int serverPort = getNextServerPort();
    printf("Starting Server_%d...\n", serverPort);
	while (1) {
		int srcPort, operation, idx, prevLen;
		Message_t msg = Receive(&ports[serverPort]);
        //  Get client request commmand
		srcPort = msg.data[SRC_PORT_IDX];
		operation = msg.data[REQ_OP_IDX];
		idx = msg.data[TABLE_ROW_IDX];

		printf("[Server_%d] Message Received: Client_%d requests ", serverPort, srcPort);
		
		int i, j;
		char prev[256];
        char recvd[256];
        //  Get table lock so no other operation modifies table while changes are in progress
        P(table);
        //  Perform requested operation
		switch(operation) {
            case ADD:
                printf("ADD ROW %d\n", idx);
                strcpy(prev, serverTable[idx]);
                getStrMsg(recvd, srcPort * 10);
                strcat(serverTable[idx], recvd);
                printf("\"%s\" --> \"%s\"\n", prev, serverTable[idx]);
                break;
            case MODIFY:
                printf("MODIFY ROW %d\n", idx);
                strcpy(prev, serverTable[idx]);
                getStrMsg(recvd, srcPort * 10);
                strcpy(serverTable[idx], recvd);
                printf("\"%s\" --> \"%s\"\n", prev, serverTable[idx]);
                break;
            case DELETE:
                printf("DELETE ROW %d\n", idx);
                strcpy(serverTable[idx], "");
                break;
            case PRINT:
                printf("READ\n");
                for (i = 0; i < 10; i++) {
                    sendStrMsg(serverTable[i], srcPort);
                }
                break;
		}
        V(table);
        //  Released table lock
        //  Server debug
        #if DEBUG
            for (i = 0; i < TABLE_ROWS; i++) {
                printf("[Server Debug Table %d] Row %d: \"%s\"\n", serverPort, i, serverTable[i]);
            }
        #endif
        //  Sleep to observe output
		sleep(1);
	}
}

//  Write client request Add/Modify/Delete operation
void writerClient() {
    //  Random text selector
	char clientTable[TABLE_ROWS][MAX_STRING_LEN] = {
			"Lorem ipsum dolor sit amet, etiam etiam,",
			"morbi libero nulla, tempor ipsum suscipit.",
			"Tincidunt vivamus penatibus, vehicula egestas et,",
			"egestas nec etiam.",
			"In quisque, turpis metus.",
			"Volutpat non, urna ac, quam ac.",
			"Suscipit velit lectus, nunc erat. ",
			"Molestie amet, convallis vitae, morbi tempor.",
			"Auctor vivamus adipiscing, molestie quis a,",
			"vestibulum orci felis.",
            "Urna non eu.",
            "Lacinia pede ut, nulla in.",
            "Optio mauris.",
            "Integer integer.",
            "Ligula fusce ut, magna vestibulum in,",
            "cursus pellentesque posuere."
	};
	int port = nextClientPort++;
    printf("Starting writer client %d -> server %d...\n", port, serverPort);
	while (1) {
		Message_t msg;
		int i, idx;

		msg.data[SRC_PORT_IDX] = port;
        //  Pick one of the three operations for write: Add, Modify or Delete
		msg.data[REQ_OP_IDX] = rand() % 3;
		msg.data[TABLE_ROW_IDX] = rand() % TABLE_ROWS;
        printf("Sending OP %d at idx %d and port %d\n", msg.data[REQ_OP_IDX], msg.data[TABLE_ROW_IDX], port * 10);
		Send(&ports[serverPort], msg);

		if (msg.data[REQ_OP_IDX] == ADD || msg.data[REQ_OP_IDX] == MODIFY) {
            //  If read or modify operation, send out text messages to follow
			idx = rand() % TABLE_ROWS;
			sendStrMsg(clientTable[idx], port * 10);
		}
	}
}

//  Reader client which reads the table
void readerClient() {
    int port = nextClientPort++;
    //int serverPort = randint(NUM_OF_SERVERS);
    printf("Starting read client %d -> server %d...\n", port, serverPort);
	while (1) {
        //  Rolling a die. Prints only if roll is 2
        int roll = rand() % 3;
        if(roll == 2) {
            Message_t msg;
            int i;

            msg.data[SRC_PORT_IDX] = port;
            msg.data[REQ_OP_IDX] = PRINT;
            Send(&ports[0], msg);

            for (i = 0; i < TABLE_ROWS; i++) {
                char s[256];
                getStrMsg(s, port);

                if (!i) {
                    printf("\n[ReaderClient] Printing strings sent from server:\n");
                }

                printf("[ReaderClient] Row %d: \"%s\"\n", i, s);
            }
            printf("\n");
        } else {
            printf("\n[ReaderClient] Skipping printing\n");
            yield();
        }
		
	}
}

int main() {
	puts("[NOTE] There is delay added to threads to make output more observable.\n[NOTE] Change value of DEBUG to 1 if you want to observe each step of process.\n[DOCS] The server listens to client request whether to add, modify, delete a given table row,\n[DOCS] or Print entire server table passed from server.\n[DOCS] Clients 1 and 2 sent a randomly picked text string to server, \n[DOCS] which server uses to modify the server table\n[DOCS] Multiple messages are used if string is too long to fit in single message\n[DOCS] Client 3 rolls a die and checks whether it is lucky number or not\n[DOCS] If roll isn't 2, Client 3 won't request server to print the entire table and yields\n[DOCS] If guess is 2, then Client 3 prints entire table");

    //  Set large seed
	srand(time(0));
    //  initialiaze table lock
    table = CreateSem(1);
    int i;
    //  Initialize ports
    for(i=0; i<NUM_PORTS; i++)
        CreatePort(&ports[i], 10); 
    //  Start server threads
    for(i=0; i<NUM_OF_SERVERS; i++)
        start_thread(server);
    //  Start write clients
    for(i=0; i<NUM_OF_WRITER_CLIENTS; i++)
        start_thread(writerClient);
    //  Start reader clients
    for(i=0; i<NUM_OF_READER_CLIENTS; i++)
        start_thread(readerClient);
	run();
    return 0;
}
