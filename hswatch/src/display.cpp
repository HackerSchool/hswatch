#include "display.h"
#include "queue_display.h"

static SemaphoreHandle_t mutex;

void Display::clear(void){

	xSemaphoreTake(mutex,portMAX_DELAY);

	msg_queue_display msg;

	msg.type=_clear;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::display(void){
	
	msg_queue_display msg;

	msg.type=_display;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

	xSemaphoreGive(mutex);

}

void Display::setPixel(int16_t x, int16_t y){}
void Display::drawLine(int16_t x0, int16_t y0, int16_t x1, int16_t y1){}

void Display::drawRect(int16_t x, int16_t y, int16_t width, int16_t height){
	msg_queue_display msg;

	msg.type=_drawRect;
	msg.a = x;
	msg.b = y;
	msg.c = width;
	msg.d = height;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);
}

void Display::fillRect(int16_t x, int16_t y, int16_t width, int16_t height){
	msg_queue_display msg;

	msg.type=_fillRect;
	msg.a = x;
	msg.b = y;
	msg.c = width;
	msg.d = height;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);
}

void Display::drawCircle(int16_t x, int16_t y, int16_t radius){}
void Display::fillCircle(int16_t x, int16_t y, int16_t radius){}

void Display::drawHorizontalLine(int16_t x, int16_t y, int16_t length){
	
	msg_queue_display msg;

	msg.type=_drawHorizontalLine;
	msg.a = x;
	msg.b = y;
	msg.c = length;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::drawVerticalLine(int16_t x, int16_t y, int16_t length){}
void Display::drawProgressBar(uint16_t x, uint16_t y, uint16_t width, uint16_t height, uint8_t progress){}
void Display::drawFastImage(int16_t x, int16_t y, int16_t width, int16_t height, const uint8_t *image){}

void Display::drawXbm(int16_t x, int16_t y, int16_t width, int16_t height, const uint8_t* xbm){

	msg_queue_display msg;

	msg.type=_drawXbm;
		msg.a = x;
	msg.b = y;
	msg.c = width;
	msg.d = height;
	msg.image = xbm;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::drawString(int16_t x, int16_t y, String text){

	msg_queue_display msg;

	msg.type=_drawString;
	msg.a = x;
	msg.b = y;

	strcpy(msg.s,text.c_str());

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::drawStringMaxWidth(int16_t x, int16_t y, int16_t maxLineWidth, String text){
	
	msg_queue_display msg;

	msg.type=_drawStringMaxWidth;
	msg.a = x;
	msg.b = y;
	msg.c = maxLineWidth;

	strcpy(msg.s,text.c_str());

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

uint16_t Display::getStringWidth(String text){}

void Display::setTextAlignment(align_text textAlignment){

	msg_queue_display msg;

	switch (textAlignment)
	{
	case left:
	msg.a=0;
	break;

	case center:
	msg.a=1;
	break;
	
	case right:
	msg.a=2;
	break;
	
	default:
	msg.a=3;
	break;
	}

	msg.type=_setTextAlignment;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::setFont(font_type font){

	msg_queue_display msg;

	switch (font)
	{
	case arial_10:
	msg.a=0;
	break;

	case arial_16:
	msg.a=1;
	break;
	
	default:
	msg.a=2;
	break;
	}

	msg.type=_setFont;

	xQueueSend(*queue_display,&msg, portMAX_DELAY);

}

void Display::initDisplay(){
	mutex = xSemaphoreCreateMutex();
}

/*
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
}*/