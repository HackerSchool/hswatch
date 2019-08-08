#ifndef Morsea_h
#define Morsea_h

#include "Arduino.h"
#include "Morse.h"

class Morsea: public Morse{
  public:
    Morsea(int size);
    void dot();
    void dash();
};

#endif
