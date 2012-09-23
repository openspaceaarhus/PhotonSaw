#pragma once

#include "defines.h"
#include <stdio.h>
#include <util/delay.h>


//  ATMega8 LCD Driver
//
//  (C) 2009 - 2012 Radu Motisan , radu.motisan@gmail.com
//  www.pocketmagic.net
//  All rights reserved.
//
//  lcd.h: Definitions for LCD command instructions
//  The constants define the various LCD controller instructions which can be passed to the 
//  function lcd_command(), see HD44780 data sheet for a complete description.


//--------------------------------CONFIGURE LCD------------------------------------------------------//
#define LCD_LINES	 2		// number of visible lines of the display
#define LCD_DISP_LENGTH  16     	// visibles characters per line of the display 
#define LCD_START_LINE1  		 0x00   // DDRAM address of first char of line 1 
#define LCD_START_LINE2  		 0x40   // DDRAM address of first char of line 2 
#define LCD_START_LINE3  		 0x14   // DDRAM address of first char of line 3 
#define LCD_START_LINE4  		 0x54   // DDRAM address of first char of line 4 

//Purpose: work with a LCD display
#define LCD_DATA0_PORT   PORTB     	// port for 4bit data bit 0 		//D5 poz:3 connector...[digits 0,1,...]
#define LCD_DATA1_PORT   PORTB     	// port for 4bit data bit 1 		//D6
#define LCD_DATA2_PORT   PORTD     	// port for 4bit data bit 2 		//D7
#define LCD_DATA3_PORT   PORTD     	// port for 4bit data bit 3 		//B0
#define LCD_DATA0_PIN    6          // pin for 4bit data bit 0  		
#define LCD_DATA1_PIN    7          // pin for 4bit data bit 1  
#define LCD_DATA2_PIN    5          // pin for 4bit data bit 2  
#define LCD_DATA3_PIN    6          // pin for 4bit data bit 3  
#define LCD_RS_PORT      PORTD     	// port for RS line         		//D3 poz:1 connector
#define LCD_RS_PIN       2          // pin  for RS line         
#define LCD_E_PORT       PORTD     	// port for Enable line     		//D4 poz:2 connector
#define LCD_E_PIN        4          // pin  for Enable line     
#define LCD_RW_PORT      PORTD // port for RW line UNUSED - we only WRITE
#define LCD_RW_PIN       3        // pin  for RW line UNUSED - we only WRITE
//---------------------------------------------------------------------------------------------------//

// instruction register bit positions, see HD44780U data sheet 
#define LCD_CLR               	0x0    	// DB0: clear display                  
#define LCD_HOME              	0x1    	// DB1: return to home position        
#define LCD_ENTRY_MODE        	0x2     // DB2: set entry mode                 
#define LCD_ENTRY_INC         	0x1     // DB1: 1=increment, 0=decrement     
#define LCD_ENTRY_SHIFT       	0x0     // DB2: 1=display shift on           
#define LCD_ON                	0x3     // DB3: turn lcd/cursor on             
#define LCD_ON_DISPLAY        	0x2     // DB2: turn display on              
#define LCD_ON_CURSOR         	0x1     // DB1: turn cursor on               
#define LCD_ON_BLINK          	0x0     // DB0: blinking cursor ?          
#define LCD_MOVE              	0x4     // DB4: move cursor/display            
#define LCD_MOVE_DISP         	0x3     // DB3: move display (0-> cursor) ?  
#define LCD_MOVE_RIGHT        	0x2     // DB2: move right (0-> left) ?      
#define LCD_FUNCTION          	0x5     // DB5: function set                   
#define LCD_FUNCTION_8BIT     	0x4     // DB4: set 8BIT mode (0->4BIT mode) 
#define LCD_FUNCTION_2LINES   	0x3     // DB3: two lines (0->one line)      
#define LCD_FUNCTION_10DOTS   	0x2     // DB2: 5x10 font (0->5x7 font)      
#define LCD_CGRAM             	0x6     // DB6: set CG RAM address             
#define LCD_DDRAM             	0x7     // DB7: set DD RAM address             
#define LCD_BUSY              	0x7     // DB7: LCD is busy                    

// set entry mode: display shift on/off, dec/inc cursor move direction 
#define LCD_ENTRY_DEC            0x04	// display shift off, dec cursor move dir 
#define LCD_ENTRY_DEC_SHIFT      0x05   // display shift on,  dec cursor move dir 
#define LCD_ENTRY_INC_           0x06   // display shift off, inc cursor move dir 
#define LCD_ENTRY_INC_SHIFT      0x07   // display shift on,  inc cursor move dir 

// display on/off, cursor on/off, blinking char at cursor position 
#define LCD_DISP_OFF             0x08   // display off                            
#define LCD_DISP_ON              0x0C   // display on, cursor off                 
#define LCD_DISP_ON_BLINK        0x0D   // display on, cursor off, blink char     
#define LCD_DISP_ON_CURSOR       0x0E   // display on, cursor on                  
#define LCD_DISP_ON_CURSOR_BLINK 0x0F   // display on, cursor on, blink char      

// move cursor/shift display 
#define LCD_MOVE_CURSOR_LEFT     0x10   // move cursor left  (decrement)          
#define LCD_MOVE_CURSOR_RIGHT    0x14   // move cursor right (increment)          
#define LCD_MOVE_DISP_LEFT       0x18   // shift display left                     
#define LCD_MOVE_DISP_RIGHT      0x1C   // shift display right                    

// function set: set interface data length and number of display lines 
#define LCD_FUNCTION_4BIT_1LINE  0x20   // 4-bit interface, single line, 5x7 dots 
#define LCD_FUNCTION_4BIT_2LINES 0x28   // 4-bit interface, dual line,   5x7 dots 
#define LCD_FUNCTION_8BIT_1LINE  0x30   // 8-bit interface, single line, 5x7 dots 
#define LCD_FUNCTION_8BIT_2LINES 0x38   // 8-bit interface, dual line,   5x7 dots 


//
#define LCD_MODE_DEFAULT     ((1<<LCD_ENTRY_MODE) | (1<<LCD_ENTRY_INC) )


// --- LCD Utils
// functional macros
#define lcd_e_high()    LCD_E_PORT  |=  _BV(LCD_E_PIN);
#define lcd_e_low()     LCD_E_PORT  &= ~_BV(LCD_E_PIN);
#define lcd_rs_high()   LCD_RS_PORT |=  _BV(LCD_RS_PIN)
#define lcd_rs_low()    LCD_RS_PORT &= ~_BV(LCD_RS_PIN)
// address of data direction register of port x
#define LCD_DDR(x) 		(*(&x - 1))      



//-----------------------------------------------------------------------------------------
// FUNCTION: _auxToggleE
// PURPOSE: flush channel E
void lcd_toggle_e(void);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_write
// PURPOSE: send a character or an instruction to the LCD
void lcd_write(uint8_t data,uint8_t rs) ;
//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_instr
// PURPOSE:  send an instruction to the LCD
void lcd_instr(uint8_t instr);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_char
// PURPOSE:  send a character to the LCD
void lcd_char(uint8_t data);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_init
// PURPOSE:  Initialize LCD to 4 bit I/O mode
void lcd_init();

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_newline
// PURPOSE:  Move cursor on specified line
void lcd_setline(uint8_t line);

extern int g_nCurrentLine;

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_string
// PURPOSE:  send a null terminated string to the LCD eg. char x[10]="hello!";
void lcd_string(const char *text);
void lcd_string_format(const char *szFormat, ...);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_gotoxy
// PURPOSE:  Set cursor to specified position
//           Input:    x  horizontal position  (0: left most position)
//                     y  vertical position    (0: first line)
void lcd_gotoxy(uint8_t x, uint8_t y);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_clrscr
// PURPOSE:  Clear display and set cursor to home position
void lcd_clrscr(void);

//-----------------------------------------------------------------------------------------
// FUNCTION: lcd_home
// PURPOSE:  Set cursor to home position
void lcd_home(void);
