#include "about.h"

void About::start(){

	rainbow_led(&led_task);
	display();

}

void About::display(){


	Display::clear();

	Display::setFont(arial_10);
	Display::setTextAlignment(center);
	Display::drawString(64, 0, "HSWatch v1.0");
	Display::drawString(64, 20, "Hacked by:");
	Display::drawString(64, 35, "Pedro Direita & Filipe Varela");
	Display::drawString(64, 50, "@ HackerSchool.io");

	Display::display();
	
}

void About::but_up_left(){

	cancel_blink_led(led_task);
	exit_app();

}

About::About(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {}