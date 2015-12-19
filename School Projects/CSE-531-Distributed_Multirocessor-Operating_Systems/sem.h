// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#pragma once
#include "threads.h"

typedef struct Semaphore {
	int val;
	Q semQ;
} Semaphore_t;

Semaphore_t *CreateSem(int initVal) {
	Semaphore_t *semaphore = (Semaphore_t *) malloc(sizeof(Semaphore_t*));
	semaphore->val = initVal;
	InitQ(&semaphore->semQ);
	return semaphore;
}

void P(Semaphore_t * sem) {
	sem->val--;
	if (sem->val < 0) {
		AddQ(&sem->semQ, DelQ(&RunQ));
	    swapcontext(&(sem->semQ->prev->context), &(RunQ->context));
	}
}

void V(Semaphore_t * sem) {
	sem->val++;
	if (sem->val <= 0) {
		AddQ(&RunQ, DelQ(&sem->semQ));
	}
	yield();
}