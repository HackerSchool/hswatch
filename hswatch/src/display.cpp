#include "display.h"

Adafruit_SSD1306 * screen;

int init_display(){

  screen = new Adafruit_SSD1306(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);
   
    // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
  if(!screen->begin(SSD1306_SWITCHCAPVCC, 0x3C)) { // Address 0x3D for 128x64
    return 1;
  }

  screen->setTextColor(WHITE);

  screen->clearDisplay();
  screen->drawBitmap(
    (screen->width()  - LOGO_WIDTH ) / 2,
    (screen->height() - LOGO_HEIGHT) / 2,
    logo_bmp, LOGO_WIDTH, LOGO_HEIGHT, 1);
  screen->display();
  delay(3000);

  return 0;
}

void default_display(String title){
  
  int s;
  String p;
  
  screen->clearDisplay();
  screen->drawFastHLine(0,12,128,WHITE);

  s = title.length()*6*DEFAULT_TITLE_SIZE;

  if(s>128){
    s = 128;
    p = title.substring(0,128);
  }else{
    p = title;
  }

  screen->setTextSize(DEFAULT_TITLE_SIZE);
  screen->setCursor((128-s)/2,0);
  screen->println(p);
  screen->display();
}