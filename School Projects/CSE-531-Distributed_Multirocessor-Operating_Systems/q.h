// TEAM MEMBERS

// Sagar Sangani     : 1205141934
// Aishwarya Sharma  : 1207616796
// Tarun Gangwani    : 1207919748
#pragma once
#include <stdio.h>
#include "TCB.h"
typedef TCB_t * Q;

void InitQ(Q *q) {
	*q = NULL;
}

void AddQ(Q *q, TCB_t *node) {
	if (*q) {
		TCB_t *head = *q;
        TCB_t *tail = (*q)->prev;

		node->prev = tail;
		node->next = head;

		tail->next = node;
		head->prev = node;
	} else {
		*q = node;
		node->prev = node;
		node->next = node;
	}
}


TCB_t *DelQ(Q *q) {
	TCB_t *node = *q;;
	if (node) {
		if (node == node->next) {
			*q = NULL;
		} else {
			*q = node->next;
		}
		node->prev->next = node->next;
		node->next->prev = node->prev;
	}
	return node;
}

void RotateQ(Q *q) {
	if (*q) {
		*q = (*q)->next;
	}
}