#include "display.h"

SSD1306Wire * screen;

int init_display(){

  screen = new SSD1306Wire(0x3c, SDA, SCL); 
   
  screen->init();

  screen->clear();
  screen->drawXbm(
    (128  - LOGO_WIDTH ) / 2,
    (64 - LOGO_HEIGHT) / 2,
    LOGO_WIDTH, LOGO_HEIGHT, logo_bmp);
  screen->display();
  delay(3000);

  return 0;
}

void default_display(String title){
  
  int s;
  String p;
  
  screen->clear();
  screen->drawHorizontalLine(0,12,128);

  screen->setFont(ArialMT_Plain_10);
  screen->setTextAlignment(TEXT_ALIGN_CENTER);

  s = screen->getStringWidth(title);

  if(s>128){
    s = 128;
    p = title.substring(0,128);
  }else{
    p = title;
  }

  screen->drawString(0, 0, p);
  screen->display();
}