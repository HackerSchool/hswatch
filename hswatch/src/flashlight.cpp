#include "flashlight.h"

void FlashLight::start(){

	state = page1;
	gain = 1;
	color = 0;
	led_status = 0;
	
	display();

}

void FlashLight::display(){

	String aux;
	String percentage = String("%");

	switch (state)
	{
	case page1:
		
		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Flashlight");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Led Status:");
		
		if(led_status==0){
			Display::drawString(64, 45, "Off");
		}else{
			Display::drawString(64, 45, "On");
		}

		Display::display();

		break;

	case page2:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Flashlight");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Color:");
		
		Display::drawString(64, 45, color_name[color]);	

		Display::display();

		break;
	
	default:

		aux=String((int)round(gain*100));

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Flashlight");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Power:");

		Display::drawString(64, 45, aux + percentage);

		Display::display();

		break;
	}
	
}

void FlashLight::but_down_left(){

	switch (state)
	{
	case page1:
		state=page2;
		display();
		break;
	
	case page2:
		state=page3;
		display();
		break;
	
	default:
		break;
	}

}

void FlashLight::but_up_left(){

	switch (state)
	{
	case page1:
		cancel_blink_led(led_task);
		exit_app();
		break;
	
	case page2:
		state=page1;
		display();
		break;
	
	default:
		state=page2;
		display();
		break;
	}

}

void FlashLight::but_up_right(){

	switch (state)
	{
	case page1:
		
		if(led_status){
			led_status = 0;
			cancel_blink_led(led_task);
		}else{
			led_status = 1;
			turnon_led(led_color[color][0]*gain, led_color[color][1]*gain, led_color[color][2]*gain, &led_task);
		}

		display();
		break;
	
	case page2:
		
		if(color==N_COLOR-1){
			color=0;
		}else{
			color++;
		}

		if(led_status)
			turnon_led(led_color[color][0]*gain, led_color[color][1]*gain, led_color[color][2]*gain, &led_task);

		display();
		break;
	
	default:
		
		if(gain==1){
			gain=0.25;
		}else{
			gain+=0.25;
		}

		if(led_status)
			turnon_led(led_color[color][0]*gain, led_color[color][1]*gain, led_color[color][2]*gain, &led_task);

		display();
		break;
	}

}

FlashLight::FlashLight(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
		

}