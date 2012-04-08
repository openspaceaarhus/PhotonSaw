#ifndef __RINGBUFFER_H__
#define __RINGBUFFER_H__

typedef struct {
  unsigned int mask;  // The mask to and the index with to cause it wrap around.
  int start; // Index of the oldest element (where the reader reads from)
  int end;   // Index just beyond the newest element (where the writer can write to)
  int endHidden; // Like end, but used for inserting hidden elements.
} RingBufferControl;

// Initializes a ring buffer to use an array with 1<<order elements
void rbInit(RingBufferControl *rb, int order);

extern char rbIsFull(RingBufferControl *rb);
extern char rbIsEmpty(RingBufferControl *rb);
extern int rbLength(RingBufferControl *rb);

// Returns the index of element to write to, will overwrite the oldest element in case of overflow
extern int rbOverWrite(RingBufferControl *rb);

// Returns the index of element to write to, will return -1 if buffer is full
extern int rbWrite(RingBufferControl *rb);

// Returns the index of the element to read from, will return -1 if buffer is empty.
extern int rbRead(RingBufferControl *rb);

extern int rbWriteHidden(RingBufferControl *rb);
extern char rbIsFullHidden(RingBufferControl *rb);
extern void rbShowHidden(RingBufferControl *rb);
extern int rbLengthHidden(RingBufferControl *rb);

// Define the ring buffer control and the array and initialize the control structure
#define RING_BUFFER(ctrl, order, type) RingBufferControl ctrl; type ctrl ## Array[1<<(order)]
#define EXTERN_RING_BUFFER(ctrl, order, type) extern RingBufferControl ctrl; extern type ctrl ## Array[1<<(order)];

// Read an element (be sure to check rbIsEmpty first or you will crash!)
#define RB_READ(ctrl) ctrl ## Array[rbRead(&ctrl)]

// Writes an element (be sure to check rbIsFull first or you will crash!)
#define RB_WRITE(ctrl, value) ctrl ## Array[rbWrite(&ctrl)]=value;

// Writes an element, possibly overwriting the oldest element
#define RB_OVERWRITE(ctrl, value) ctrl ## Array[rbOverWrite(&ctrl)]=value;



#endif
