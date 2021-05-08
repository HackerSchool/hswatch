#include "settings.h"

void Settings::start(){

	state = led;	
	
	display();

}

void Settings::display(){

	int led_status, buzzer_status, vibrator_status;

	switch (state)
	{
	case led:
		
		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Settings");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Led Enable:");

		led_status = enable_led();

		if(led_status==DISABLE_LED){
			Display::drawString(64, 45, "Off");
		}else if(led_status==ENABLE_HIGH_PRIORITY_LED){
			Display::drawString(64, 45, "Only for apps");
		}else{
			Display::drawString(64, 45, "On");
		}

		Display::display();

		break;

	case buzzer:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Settings");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Buzzer Enable:");
		
		buzzer_status = enable_buzzer();

		if(buzzer_status==DISABLE_BUZZER){
			Display::drawString(64, 45, "Off");
		}else{
			Display::drawString(64, 45, "On");
		}

		Display::display();

		break;

	case vibrator:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Settings");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "Vibrator Enable:");
		
		vibrator_status = enable_vibrator();

		if(vibrator_status==DISABLE_VIBRATOR){
			Display::drawString(64, 45, "Off");
		}else{
			Display::drawString(64, 45, "On");
		}

		Display::display();

		break;

	default:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Settings");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "About");

		Display::setFont(arial_10);
		Display::drawString(64, 45, "click OK");

		Display::display();

		break;
	}
	
}

void Settings::but_down_left(){

	switch (state)
	{
	case led:
		state=buzzer;
		display();
		break;
	
	case buzzer:
		state=vibrator;
		display();
		break;

	case vibrator:
		state=about;
		display();
		break;
	
	default:
		break;
	}

}

void Settings::but_up_left(){

	switch (state)
	{
	case led:
		exit_app();
		break;
	
	case buzzer:
		state=led;
		display();
		break;
	
	case vibrator:
		state=buzzer;
		display();
		break;
	
	default:
		state=vibrator;
		display();
		break;
	}

}

void Settings::but_up_right(){

	int led_status, buzzer_status, vibrator_status;

	switch (state)
	{
	case led:

		led_status = enable_led();
		
		if(led_status==DISABLE_LED){
			enable_led(ENABLE_HIGH_PRIORITY_LED);
		}else if(led_status==ENABLE_HIGH_PRIORITY_LED){
			enable_led(ENABLE_LED);
		}else{
			enable_led(DISABLE_LED);
		}

		display();
		break;
	
	case buzzer:
		
		buzzer_status = enable_buzzer();
		
		if(buzzer_status==DISABLE_BUZZER){
			enable_buzzer(ENABLE_BUZZER);
		}else{
			enable_buzzer(DISABLE_BUZZER);
		}

		display();
		break;

	case vibrator:
		
		vibrator_status = enable_vibrator();
		
		if(vibrator_status==DISABLE_VIBRATOR){
			enable_vibrator(ENABLE_VIBRATOR);
		}else{
			enable_vibrator(DISABLE_VIBRATOR);
		}

		display();
		break;
	
	default:
		
		run_app("About");
		break;
	}

}

Settings::Settings(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
		

}