#include "notification.h"
#include "display.h"
#include "home.h"

SemaphoreHandle_t mutex;

void Notification::start(){

	xSemaphoreTake(mutex,portMAX_DELAY);

	if(notification_list.empty()){
		xSemaphoreGive(mutex);
		exit_app();
		return;
	}

	index=notification_list.begin();
	notification_number=1;

	display();

	xSemaphoreGive(mutex);
}

void Notification::display(){

	unsigned char hour, minute;
	String title, text, s="", s2="";

	hour = (*index).hour;
	minute = (*index).minute;
	title = *((*index).title);
	text = *((*index).text);

	Display::clear();

	Display::setFont(arial_16);
	Display::setTextAlignment(center);
	Display::drawString(64, 0, title);

	Display::drawHorizontalLine(0,12,128);

	Display::setFont(arial_16);
	Display::setTextAlignment(both);
	Display::drawString(64, 32, text);
  

	if(hour<10){
		s=s+"0";
		s=s+String(hour);
	}else{
		s=s+String(hour);
	}

	s=s+":";

	if(minute<10){
		s=s+"0";
		s=s+String(minute);
	}else{
		s=s+String(minute);
	}

	s2 = String(notification_number) + "/" + String(notification_list.size());

	Display::drawHorizontalLine(0,51,128);
	
	Display::setFont(arial_10);
	Display::setTextAlignment(right);
	Display::drawString(0, 53, s);
	Display::setTextAlignment(left);
	Display::drawString(0, 53, s2);

	Display::display();
	
}

//void Notification::but_up_left(){}

//void Notification::but_up_right(){}

//void Notification::but_down_left(){}

//void Notification::but_down_right(){}

void Notification::bt_receive(char* message){

	Serial.println("received notification");

	notification * new_not = (notification*) malloc(sizeof(notification)); 

	char * str, * context, delim[2];

	delim[0] = (char)0x03;
	delim[1] = '\0';

	str = strtok_r(message,delim,&context);
	if(str==NULL)
		return;

	new_not->logo = new String(str);

	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not->hour=atoi(str);

	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not->minute=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not->title = new String(str);

	str = strtok_r(NULL,"\0",&context);
		if(str==NULL)
		return;
	new_not->text = new String(str);
	
	xSemaphoreTake(mutex,portMAX_DELAY);

	notification_list.push_front((*new_not));

	xSemaphoreGive(mutex);

	notification n = notification_list.front();

	Home* home =(Home*) App::app_search_by_name("Home");

	home->notify(*(new_not->title), *(new_not->text), *(new_not->logo));
}

Notification::Notification(String id_in, String name_in, unsigned char* logo_in): App(id_in,name_in,logo_in) {
		mutex = xSemaphoreCreateMutex();
}