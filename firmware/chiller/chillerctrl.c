#include "defines.h"

#include <ctype.h>
#include <inttypes.h>

#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>

#include <avr/io.h>
#include <util/delay.h>
#include <avr/pgmspace.h>
#include <avr/sleep.h>

#include <avr/wdt.h> 
#include <avr/interrupt.h>
#include <avr/eeprom.h> 

#include "uart.h"

#include "pwmvoltages.h"
#include "adchelper.h"

#include "lcd.h"

// We don't really care about unhandled interrupts.
EMPTY_INTERRUPT(__vector_default)

// A macro and function to store string constants in flash and only copy them to
// RAM when needed, note the limit on string length.

char stringBuffer[80];

const char *getString(PGM_P src) {
    strcpy_P(stringBuffer, src);
    return stringBuffer;
}

#define PROGSTR(s) getString(PSTR(s))

void led(char on) {
  if (on) {
    PORTB |= _BV(PB5);   
  } else {
    PORTB &=~ _BV(PB5);   
  }
}


#define INPUT_BUFFER_SIZE 40
char inputBuffer[INPUT_BUFFER_SIZE];
char *inputBufferEnd;

void resetInputBuffer() {
  memset(inputBuffer, 0, INPUT_BUFFER_SIZE);
  inputBufferEnd = inputBuffer;
}

// Read/write parameters
#define P_POWER 0
#define P_STORE_MAX_TEMP 1
#define P_STORE_MIN_TEMP 2
#define P_CIRCULATION_TEMP 3
#define P_FAN_POST_RUN 4

#define P_RW_COUNT 5


// Read only parameters
#define P_CURRENT_STATE 5
#define P_FAN_TIMER 6

#define P_COUNT 7

int parameters[P_COUNT];

int DEFAULT_PARAMETERS[P_RW_COUNT] = {
  0,   // power Off
  15,  // max degrees in cold-store 
  -10, // min degrees in cold-store
  20,  // output temperture 
  60   // Number of seconds to run the fan after the compressor has stopped.
};

/*
  printf(PROGSTR("current-store: %.1f\r\n"), currentStore);
  printf(PROGSTR("current-circulation: %.f\r\n"), currentCirculation);
  
  printf(PROGSTR("cooling-pwm: %d\r\n"), (int)floor(getCurrentCoolingSpeed()));
  printf(PROGSTR("circulation-pwm: %d\r\n"), (int)floor(getCurrentCirculationSpeed()));

  printf(PROGSTR("compressor: %d\r\n"), (PORTC & _BV(PC5)) ? 1 : 0); 
  printf(PROGSTR("fan: %d\r\n"), (PORTD & _BV(PD7)) ? 1 : 0); 
  printf(PROGSTR("tankpump: %d\r\n"), (PORTB & _BV(PB0)) ? 1 : 0); 
*/

char PN_POWER[] PROGMEM = "power";
char PN_STORE_MAX_TEMP[] PROGMEM = "store-max";
char PN_STORE_MIN_TEMP[] PROGMEM = "store-min";
char PN_CIRCULATION_TEMP[] PROGMEM = "circ-set";
char PN_FAN_POST_RUN[] PROGMEM = "fan-run";
char PN_CURRENT_STATE[] PROGMEM = "state";
char PN_FAN_TIMER[] PROGMEM = "fan-timer";

PGM_P PARAMETER_NAMES[P_COUNT] PROGMEM = {
  [P_POWER]            = PN_POWER,
  [P_STORE_MAX_TEMP]   = PN_STORE_MAX_TEMP,
  [P_STORE_MIN_TEMP]   = PN_STORE_MIN_TEMP,
  [P_CIRCULATION_TEMP] = PN_CIRCULATION_TEMP,
  [P_FAN_POST_RUN]     = PN_FAN_POST_RUN,
  [P_CURRENT_STATE]    = PN_CURRENT_STATE,
  [P_FAN_TIMER]        = PN_FAN_TIMER,
};

char *getParameterName(int index) {
  strcpy_P(stringBuffer, (PGM_P)pgm_read_word(&(PARAMETER_NAMES[index])));
  return stringBuffer;
}

float currentStore = 0;
float currentCirculation = 0;

void printState() {  
  for (int i=0;i<P_COUNT;i++) {
    printf("p%d\t%d\t%s\r\n", i, parameters[i], getParameterName(i));
  }
  /*
  printf(PROGSTR("current-store: %.1f\r\n"), currentStore);
  printf(PROGSTR("current-circulation: %.f\r\n"), currentCirculation);
  
  printf(PROGSTR("cooling-pwm: %d\r\n"), (int)floor(getCurrentCoolingSpeed()));
  printf(PROGSTR("circulation-pwm: %d\r\n"), (int)floor(getCurrentCirculationSpeed()));
  */
  /*
  printf(PROGSTR("compressor: %d\r\n"), (PORTC & _BV(PC5)) ? 1 : 0); 
  printf(PROGSTR("fan: %d\r\n"), (PORTD & _BV(PD7)) ? 1 : 0); 
  printf(PROGSTR("tankpump: %d\r\n"), (PORTB & _BV(PB0)) ? 1 : 0); 
  */
  printf(PROGSTR("\r\n"));


  //printf("\r\n");
  //puts(PROGSTR("\r\n"));
}


#define STATE_OFF 0
#define STATE_ON 1
#define STATE_FAN_ON 2
#define STATE_COMPRESSOR_ON 3

//char currentState = STATE_OFF;

void setState(char state) {
  parameters[P_CURRENT_STATE] = state;

  if (state == STATE_OFF) { // Doing nothing
    parameters[P_POWER] = 0;

    PORTC &=~ _BV(PC5);  // Compressor Relay
    PORTD &=~ _BV(PD7);  // Fan Relay
    PORTB &=~ _BV(PB0);  // Tank circulation pump Relay
    setCirculationSpeed(0);    
    setCoolingSpeed(0);    

  } else if (state == STATE_ON) {
    PORTC &=~ _BV(PC5);  // Compressor Relay
    PORTD &=~ _BV(PD7);  // Fan Relay
    PORTB |=  _BV(PB0);  // Tank circulation pump Relay
    setCirculationSpeed(100);        

  } else if (state == STATE_FAN_ON) {
    PORTC &=~ _BV(PC5);  // Compressor Relay
    PORTD |=  _BV(PD7);  // Fan Relay
    PORTB |=  _BV(PB0);  // Tank circulation pump Relay
    setCirculationSpeed(100);        

  } else if (state == STATE_COMPRESSOR_ON) {
    PORTC |= _BV(PC5);  // Compressor Relay
    PORTD |= _BV(PD7);  // Fan Relay
    PORTB |= _BV(PB0);  // Tank circulation pump Relay
    setCirculationSpeed(100);        
  } 
}



char ledToggle;

void handleInputLine() {
  led(ledToggle); 
  ledToggle = ledToggle ? 0 : 1;

  char *value= strchr(inputBuffer, '=');
  if (value) {
    *value = 0; // Zero terminate the key.
    value++;    
    int val = atoi(value);

    for (int i=0;i<P_RW_COUNT;i++) {
      if (!strcmp(inputBuffer, getParameterName(i))) {
	parameters[i] = val;
	printf(PROGSTR("Ok: Set p%d = %d\r\n"), i, val);	
      }
    }
	    
  } else if (*inputBuffer) {
    printf(PROGSTR("Fail '%s'\r\n"), inputBuffer);
  } else {
    printf(PROGSTR("Status:\r\n"));	
  }
  
  printState();
}

void updateDisplay() {
  lcd_gotoxy(0,0);
  if (parameters[P_CURRENT_STATE] == STATE_OFF) {
    lcd_puts(PROGSTR("  pSaw Chiller   "));
    lcd_gotoxy(0,1);
    lcd_puts(PROGSTR("    Standby      "));
    return;

  } else if (parameters[P_CURRENT_STATE] == STATE_ON) {
    lcd_puts(PROGSTR(" Chiller online  "));

  } else if (parameters[P_CURRENT_STATE] == STATE_FAN_ON) {
    lcd_puts(PROGSTR("   Running fan   "));

  } else if (parameters[P_CURRENT_STATE] == STATE_COMPRESSOR_ON) {
    lcd_puts(PROGSTR("   Compressing!  "));
  }
 
  char buffy[17];
  memset(buffy, 0, sizeof(buffy));
  lcd_gotoxy(0,1);
  sprintf(buffy, PROGSTR("s=%d \xdf" "C o=%d \xdf" "C"), (int)floor(currentStore), (int)floor(currentCirculation));
  lcd_puts(buffy);
}

void pollInput() {
  
  if (UCSR0A & _BV(RXC0)) {
    char ch = UDR0;

    if (ch == '\r') {
      handleInputLine();
      resetInputBuffer();

    } else {
      *inputBufferEnd = ch;
      inputBufferEnd++;

      if (inputBufferEnd == inputBuffer + INPUT_BUFFER_SIZE -1) {
	
	printf(PROGSTR("ERROR: linebuffer overflow\n"));
	resetInputBuffer();
      }      
    }  
  }
}

#define AVCC_MV 4650.0
#define NTC_PULLUP 10000
#define NTC_PULLUP_VOLTAGE (AVCC_MV/1000)

// Constants from the datasheet for the NTC used:
// ELFA:60-279-24 (1% 5k RH16 6D502)
#define NTC_B50 3936.0
#define NTC_R25 5000.0

unsigned int readADCmv(const int channel) {
  unsigned int raw = getOsADC(channel);

  return (AVCC_MV*raw) / (1<<10);
}

float readNTCres(const int channel) {
  float v = readADCmv(channel);
  v /= 1000;
  return -(NTC_PULLUP * v) / (v-NTC_PULLUP_VOLTAGE);
}

float r2c(float r) {
  return 1/(1/(25+273.15) + 1/NTC_B50 * log(r/NTC_R25))-273.15;
}

float readNTCcelcius(const int channel) {
  return r2c(readNTCres(channel));
}

#define P 0.07
#define I 0.0001
#define D 0.005

#define MAX_OUTPUT 100
#define MIN_OUTPUT 0

float errorSum = 0;
float lastError = 0;
float output = 0;

unsigned int longpwm = 0;
unsigned char timer = 0;

#define LONG_PWM_TICK 5*100
#define MIN_SPEED 50

void updateStateMachine() {

  float thisStore = readNTCcelcius(0);
  currentStore += (thisStore-currentStore)*0.1;

  float thisCirculation = readNTCcelcius(1);
  currentCirculation += (thisCirculation-currentCirculation)*0.1;

  if (parameters[P_CURRENT_STATE] == STATE_OFF) {
    if (parameters[P_POWER]) {
      setState(STATE_ON);
    }

  } else {

    // This is the PID regulation of the circulation temperature
    float error = currentCirculation - parameters[P_CIRCULATION_TEMP];
    errorSum += error;
  
    output = output
      - P*error 
      - I*errorSum
      - D*(error-lastError);

    lastError = error;

    if (output > MAX_OUTPUT) {
      output = MAX_OUTPUT;
      
    } else if (output < MIN_OUTPUT) {
      output = MIN_OUTPUT;
    }
    
    if (output < MIN_SPEED) {
      if (longpwm++ > LONG_PWM_TICK) {
		longpwm = 0;
      }

      if (longpwm < (int)floor(LONG_PWM_TICK*output/MIN_SPEED)) {
		setCoolingSpeed(MIN_SPEED);
      } else {
		setCoolingSpeed(0);
      }      

    } else {
      setCoolingSpeed(output);
    }
  }

  if (parameters[P_CURRENT_STATE] == STATE_ON) {
    if (parameters[P_POWER]) {
      
      if (currentStore > parameters[P_STORE_MAX_TEMP]) {
	setState(STATE_COMPRESSOR_ON);
      }      

    } else {
      setState(STATE_OFF);
    }
  }

  if (parameters[P_CURRENT_STATE] == STATE_COMPRESSOR_ON) {
   
    if (currentStore < parameters[P_STORE_MIN_TEMP] || !parameters[P_POWER]) {
      setState(STATE_FAN_ON);
      parameters[P_FAN_TIMER] = parameters[P_FAN_POST_RUN];
      timer = 0;
    }      

  }

  if (parameters[P_CURRENT_STATE] == STATE_FAN_ON) {
    if (++timer >= 100) {
      timer = 0;
      if (parameters[P_FAN_TIMER] > 0) {
	parameters[P_FAN_TIMER]--;
      }

      if (parameters[P_FAN_TIMER] <= 0) {
	setState(STATE_ON);
      }
    }
  }

}


int main(void) {
  wdt_enable(WDTO_4S); // We don't want to hang.

  // Set up the outputs:
  DDRB  |= _BV(PB5);  // LED output on SCK pin

  DDRC  |= _BV(PC5);  // Compressor Relay
  DDRD  |= _BV(PD7);  // Fan Relay
  DDRB  |= _BV(PB0);  // Tank circulation pump Relay

  led(1);

  initADC();
  initPWM();
  resetInputBuffer();

  uart_init();
  FILE uart_str = FDEV_SETUP_STREAM(uart_putchar, uart_getchar, _FDEV_SETUP_RW);
  stdout = stdin = &uart_str;
  fprintf(stdout, PROGSTR("#Power up!\r\n"));

  lcd_init(LCD_DISP_ON);
    
  led(0);

  char frame = 0;

  for (int i=0;i<P_RW_COUNT;i++) {
    parameters[i] = DEFAULT_PARAMETERS[i];
  }
 
  setState(STATE_OFF);
  updateDisplay();

  while(1) {

    if ((frame & 15) == 0) {
      updateDisplay();
    } 

    pollInput();
    updateStateMachine();
    updatePWM();
    _delay_ms(10);
    wdt_reset();
    frame++;
  }	
}
