// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#pragma once
#include <ucontext.h>
#include <string.h>

typedef struct TCB {
	int id;	
	ucontext_t context;
    struct TCB *prev;
	struct TCB *next;
} TCB_t;

void init_TCB (TCB_t *tcb, void *function, void *stackP, int stack_size) {
    static size_t g_tid = 0;
    tcb->id = ++g_tid;
    
    memset(tcb, '\0', sizeof(TCB_t));
    getcontext(&tcb->context);
    tcb->context.uc_stack.ss_sp = stackP;
    tcb->context.uc_stack.ss_size = (size_t) stack_size;
    makecontext(&tcb->context, function, 0);
}