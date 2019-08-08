#include "Morsea.h"

Morsea::Morsea(int size): Morse(size) {}

void Morsea::dot(){
  display.print("x");
  display.display();
}

void Morsea::dash(){
  display.print("0");
  display.display();
}
