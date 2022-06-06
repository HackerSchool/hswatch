#include "quotes.h"

void Quotes::start(){

	char buf[4]="QUO";

	if(state==loading){
		send_bt(buf,4);
	}else if(state==page2){
		state=page1;
	}

	display();

}

void Quotes::display(){

	switch (state)
	{
	case page1:
		
		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, quote_title);

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		
		if(quote_text.length()>65){
			Display::drawStringMaxWidth(64, 13, 128, quote_text.substring(0,65)+" ...");
		}else{
			Display::drawStringMaxWidth(64, 13, 128, quote_text);
		}

		Display::display();

		break;

	case page2:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, quote_title);

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		
		Display::drawStringMaxWidth(64, 13, 128, quote_text.substring(65,quote_text.length()));

		Display::display();

		break;
	
	default:

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Quotes");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 25, "Loading");

		Display::drawHorizontalLine(0,51,128);

		Display::display();

		break;
	}

}
		
void Quotes::but_up_left(){
	switch (state)
	{
	case page1:
		exit_app();
		break;
	
	case page2:
		state=page1;
		display();
		break;
	
	default:
		exit_app();
		break;
	}
}
		
void Quotes::but_up_right(){
	char buf[4]="QUO";

	send_bt(buf,4);
}
		
void Quotes::but_down_left(){
	switch (state)
	{
	case page1:
		if(quote_text.length()>65){
			state=page2;
			display();
		}
		break;
	
	case page2:
		break;
	
	default:
		break;
	}

}

void Quotes::bt_receive(char* message){
	char * str, * context, delim[2];

	delim[0] = (char)0x03;
	delim[1] = '\0';

	str = strtok_r(message,delim,&context);
	if(str==NULL)
		return;

	quote_title=String(str);

	Serial.println(str);


	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;

	quote_text=String(str);
		
	Serial.println(str);
		
	state=page1;
	display();

}

Quotes::Quotes(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
		state=loading;
}