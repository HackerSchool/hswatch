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

	if (text.length()>65){
		text = text.substring(0,65)+"...";
	}

	Display::clear();

	Display::setFont(arial_10);
	Display::setTextAlignment(center);
	Display::drawString(64, 0, title);

	Display::drawHorizontalLine(0,12,128);

	Display::setFont(arial_10);
	Display::setTextAlignment(center);
	Display::drawStringMaxWidth(64, 13, 128, text);
  

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

	Serial.println(s);

	s2 = String(notification_number) + "/" + String(notification_list.size());

	Display::drawHorizontalLine(0,51,128);
	
	Display::setFont(arial_10);
	Display::setTextAlignment(right);
	Display::drawString(127, 53, s);
	Display::setTextAlignment(left);
	Display::drawString(0, 53, s2);

	Display::display();
	
}

void Notification::but_up_left(){
	if(notification_number==1){
		App::exit_app();
	}else{
		notification_number--;
		index--;
		display();
	}
}

void Notification::but_up_right(){

	int count_item=0;

	for(std::list<notification>::iterator i=notification_list.begin(); i!=notification_list.end() ;i++){
		if(*(index->logo)==*(i->logo)){
			count_item++;
		}
	}
	
	if(count_item<2){
		Home* home =(Home*) App::app_search_by_name("Home");
		home->delete_notification(*(index->logo));
	}

	delete index->text;
	delete index->title;
	delete index->logo;

	std::list<notification>::iterator prev=index;

	if(notification_number==1){
		xSemaphoreTake(mutex,portMAX_DELAY);
		notification_list.erase(index);
		xSemaphoreGive(mutex);
		App::exit_app();
	}else{
		notification_number--;
		prev--;
		xSemaphoreTake(mutex,portMAX_DELAY);
		notification_list.erase(index);
		xSemaphoreGive(mutex);
		index=prev;
		display();
	}

}

void Notification::but_down_left(){

	index++;
	if(index!=notification_list.end()){
		notification_number++;
		display();
	}else{
		index--;
	}

}

//void Notification::but_down_right(){}

void Notification::bt_receive(char* message){

	Serial.println("received notification");

	notification new_not; 

	char * str, * context, delim[2];

	delim[0] = (char)0x03;
	delim[1] = '\0';

	str = strtok_r(message,delim,&context);
	if(str==NULL)
		return;

	new_not.logo = new String(str);

	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not.hour=atoi(str);

	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not.minute=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	new_not.title = new String(str);

	str = strtok_r(NULL,"\0",&context);
		if(str==NULL)
		return;
	new_not.text = new String(str);
	
	xSemaphoreTake(mutex,portMAX_DELAY);

	notification_list.push_front(new_not);

	xSemaphoreGive(mutex);

	Home* home =(Home*) App::app_search_by_name("Home");

	home->notify(*(new_not.title), *(new_not.text), *(new_not.logo));
}

Notification::Notification(String id_in, String name_in, unsigned char* logo_in): App(id_in,name_in,logo_in) {
		mutex = xSemaphoreCreateMutex();
}