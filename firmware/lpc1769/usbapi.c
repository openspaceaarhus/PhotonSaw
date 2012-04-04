#include "usbapi.h"
#include "stdio.h"

RING_BUFFER(usbLineBuffer, USB_LINE_BUFFER_ORDER, char) IN_IRAM1;
RING_BUFFER(usbTxBuffer, USB_TX_BUFFER_ORDER, char) IN_IRAM1;

void usbAPIInit() {
	rbInit(&usbLineBuffer, USB_LINE_BUFFER_ORDER);
	rbInit(&usbTxBuffer, USB_TX_BUFFER_ORDER);
}

// Callback called when a full line has been buffered by the USB CDC layer.
void __attribute__ ((weak)) usbLine(char *line, unsigned int lineSize) {
  fprintf(stderr, "Ignoring line from USB: %s\n", line);
}

// Function to send data via USB
void usbSend(char *data, unsigned int dataSize) {
	while (dataSize--) {
		while (rbIsFull(&usbTxBuffer)); // Wait for the buffer to empty
		RB_WRITE(usbTxBuffer, *(data++));
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
		while (rbIsFull(&usbLineBuffer)); // Wait for the buffer to empty
		RB_WRITE(usbLineBuffer, *(data++));
	}
}

