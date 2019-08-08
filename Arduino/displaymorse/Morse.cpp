#include "Morse.h"

Morse::Morse(int size){
  _size = size;
  Serial.begin(9600);
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  display.clearDisplay();
  display.setTextSize(_size);
  display.setTextColor(WHITE);
  display.setCursor(0,0);
}

void Morse::dot(){
  display.print(".");
  display.display();
}

void Morse::dash(){
  display.print("-");
  display.display();
}

