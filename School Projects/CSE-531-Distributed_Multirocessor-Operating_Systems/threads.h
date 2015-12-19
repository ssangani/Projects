// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#include "q.h"
#include <stdlib.h>
#define STACK_SIZE 8192

Q RunQ;

void start_thread(void (*function)(void)) {
	void *stackP = malloc(STACK_SIZE);
	TCB_t *tcb = (TCB_t *) malloc(sizeof (TCB_t));
	init_TCB(tcb, function, stackP, STACK_SIZE);
	AddQ(&RunQ, tcb);
}

void run() {
    ucontext_t parent;
    getcontext(&parent);
    swapcontext(&parent, &(RunQ->context));
}

void yield() {
    RotateQ(&RunQ);
    swapcontext(&(RunQ->prev->context), &(RunQ->context));
}