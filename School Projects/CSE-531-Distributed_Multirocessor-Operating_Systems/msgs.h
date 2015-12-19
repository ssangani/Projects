// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#pragma once
#include "sem.h"
//#define NUM_PORTS 100

typedef struct {
    int data[10];
    int validMsg;
    int replyPort;
} Message_t;

typedef struct {
    Message_t msgs[10];
    Semaphore_t *mutex, *consumer, *producer;
    int readIdx, writeIdx, maxSize;
} Port_t;

//Port_t ports[NUM_PORTS];

void CreatePort(Port_t* port, int maxSize) {
    port->mutex = CreateSem(1);
	port->producer  = CreateSem(0);
	port->consumer = CreateSem(maxSize);
	port->readIdx = 0;
	port->writeIdx = 0;
    port->maxSize = maxSize;
} 

void Send(Port_t* port, Message_t msg) {
	P(port->consumer);
        P(port->mutex);

        memcpy(&port->msgs[port->writeIdx], &msg, sizeof(msg));
        port->writeIdx = (port->writeIdx + 1) % port->maxSize;

        V(port->mutex);
	V(port->producer);
}

Message_t Receive(Port_t* port) {
	P(port->producer);
        P(port->mutex);
        Message_t msg;
        memcpy(&msg, &port->msgs[port->readIdx], sizeof(Message_t));
        port->readIdx = (port->readIdx + 1) % port->maxSize;

        V(port->mutex);
	V(port->consumer);
	return msg;       
}
