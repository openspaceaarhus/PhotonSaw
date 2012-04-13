#ifndef __USBAPI_H
#define __USBAPI_H

#include "board.h"

// This is the maximum input line size
#define USB_LINE_BUFFER_SIZE 1<<12

// returns true if connected to a host 
char usbConnected();

// Callback called when a full line has been buffered by the USB CDC layer.
void usbLine(char *line, unsigned int lineSize);

// Function to send data via USB
void usbSend(const char *data, unsigned int dataSize);
void usbSendFlush(const char *data, unsigned int dataSize);
void usbFlush();

// Initializes the USB subsystem, called from initAPI()
void usbInit();

// Internal functions called by the USB CDC layer

// Read up to dataSize chars from the transmit buffer
int usbPopForTransmit(unsigned char *data, int dataSize);

// Write data from the USB buffer to the receive buffer
void usbPushReceived(unsigned char *data, int dataSize);

#endif
