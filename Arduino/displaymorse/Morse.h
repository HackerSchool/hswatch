#ifndef Morse_h
#define Morse_h

#include "Arduino.h"

#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

extern Adafruit_SSD1306 display;

#if (SSD1306_LCDHEIGHT != 64)
#error("Height incorrect, please fix Adafruit_SSD1306.h!");
#endif

class Morse
{
  public:
    Morse(int size);
    virtual void dot();
    virtual void dash();
  private:
    int _size;
};

#endif
