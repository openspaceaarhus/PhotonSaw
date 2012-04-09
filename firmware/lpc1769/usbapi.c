#include <stdio.h>

#include "usbapi.h"
#include "ringbuffer.h"

#include "usb/usb.h"
#include "usb/usbcfg.h"
#include "usb/usbhw.h"
#include "usb/usbcore.h"
#include "usb/cdc.h"
#include "usb/cdcuser.h"

// This is the maximum command line size
#define USB_LINE_BUFFER_SIZE 1<<13

// This buffer only needs to be large enough to allow a full packet to be assembled
#define USB_TX_BUFFER_ORDER 8

RING_BUFFER(usbTxBuffer, USB_TX_BUFFER_ORDER, char) IN_IRAM1;

char usbLineBuffer[USB_LINE_BUFFER_SIZE] IN_IRAM1;
int usbLineLength;

void usbInit() {
  usbLineLength = 0;
  rbInit(&usbTxBuffer, USB_TX_BUFFER_ORDER);
  CDC_Init();

  USB_Init();
  USB_Connect(TRUE);
}

// Callback called when a full line has been buffered by the USB CDC layer.
void WEAK usbLine(char *line, unsigned int lineSize) {
  fprintf(stderr, "Ignoring line from USB: %s\n\r", line);
}

char usbConnected() {
  return USB_Configuration;
}


/*
  TODO: We call CDC_BulkIn here, but we'd rather that it was called as soon as
  the hardware is ready for more data, but how do you make that happen?

  Never mind, for now we'll just let the packet sending code operate in sync,
  there's not a great amount of data to send anyway.
*/


// Function to send data via USB
void usbSend(const char *data, unsigned int dataSize) {
  if (!USB_Configuration) {
    return;
  }

  while (dataSize--) {
    while (rbIsFull(&usbTxBuffer)) CDC_BulkIn(); // Wait for the buffer to empty enough
    RB_WRITE(usbTxBuffer, *(data++));
  }  
  
  // Only send all the full packets we can.
  while (rbLength(&usbTxBuffer) > 63) { 
    CDC_BulkIn();
  }
}

void usbSendFlush(const char *data, unsigned int dataSize) {
  if (!USB_Configuration) {
    return;
  }

  while (dataSize--) {
    while (rbIsFull(&usbTxBuffer)) CDC_BulkIn(); // Wait for the buffer to empty
    RB_WRITE(usbTxBuffer, *(data++));
  }  

  usbFlush();
}

void usbFlush() {
  while (!rbIsEmpty(&usbTxBuffer)) { // send all the chars!!!
    CDC_BulkIn();
  }
}


// Internal functions called by the USB CDC layer

// Read up to dataSize chars from the transmit buffer
int usbPopForTransmit(unsigned char *data, int dataSize) {
  int res = 0;
  while (dataSize && !rbIsEmpty(&usbTxBuffer)) {
    dataSize--;
    res++;
    *data = RB_READ(usbTxBuffer);
    data++;
  }
  
  return res;
}

// Write data from the USB buffer to the receive buffer
void usbPushReceived(unsigned char *data, int dataSize) {
  while (dataSize--) {
    unsigned char ch = *(data++);

    if (ch == '\r' || ch == '\n' || usbLineLength == USB_LINE_BUFFER_SIZE) {
      usbLineBuffer[usbLineLength] = 0;
      usbLine(usbLineBuffer, usbLineLength);
      usbLineLength=0;

    } else {
      usbLineBuffer[usbLineLength++] = ch;
    }
  }
}

