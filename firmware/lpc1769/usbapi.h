#ifndef __USBAPI_H
#define __USBAPI_H

#include "board.h"
#include "ringbuffer.h"

// This is the maximum command line 
#define USB_LINE_BUFFER_SIZE 1<<13
#define USB_TX_BUFFER_ORDER 12

EXTERN_RING_BUFFER(usbTxBuffer, USB_TX_BUFFER_ORDER, char);

// Callback called when a full line has been buffered by the USB CDC layer.
void usbLine(char *line, unsigned int lineSize);

// Function to send data via USB
void usbSend(char *data, unsigned int dataSize);


// Initializes the USB subsystem, called from initAPI()
void usbInit();

// Internal functions called by the USB CDC layer

// Read up to dataSize chars from the transmit buffer
int usbPopForTransmit(unsigned char *data, int dataSize);

// Write data from the USB buffer to the receive buffer
void usbPushReceived(unsigned char *data, int dataSize);

#endif
