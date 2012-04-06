#include "ringbuffer.h"

// Initializes a ring buffer to use an array with 1<<order elements
void rbInit(RingBufferControl *rb, int order) {
	rb->mask = (1<<order) -1;
	rb->start = 0;
	rb->end = 0;
}

inline char rbIsFull(RingBufferControl *rb) {
  return ((rb->end + 1) & rb->mask) == rb->start;
}

inline char rbIsEmpty(RingBufferControl *rb) {
    return rb->end == rb->start;
}

inline int rbLength(RingBufferControl *rb) {
  return (rb->end-rb->start) & rb->mask;
}

// Returns the index of element to write to, will overwrite the oldest element in case of overflow
inline int rbOverWrite(RingBufferControl *rb) {
		int res = rb->end;
    rb->end = (rb->end + 1) & rb->mask;
    if (rb->end == rb->start) {
        rb->start = (rb->start + 1) & rb->mask; /* full, overwrite */
    }
    return res;
}

// Returns the index of element to write to, will return -1 if buffer is full
inline int rbWrite(RingBufferControl *rb) {
		int res = rb->end;
		int newEnd = (rb->end + 1) & rb->mask;
    if (newEnd == rb->start) {
        return -1;
    } else {
      rb->end = newEnd;
    	return res;
    }
}

// Returns the index of the element to read from, will return -1 if buffer is empty.
inline int rbRead(RingBufferControl *rb) {
		if (rb->start == rb->end) {
			return -1;
		}

		int res = rb->start;
    rb->start = (rb->start + 1) & rb->mask;
    return res;
}
