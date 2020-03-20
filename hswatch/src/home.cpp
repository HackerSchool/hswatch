#include "home.h"
#include "display.h"
#include "logo_notification.h"

const unsigned char n_days_in_month[12] = {31,28,31,30,31,30,31,31,30,31,30,31};
const char week_day_name[7][4] = {"DOM","SEG","TER","QUA","QUI","SEX","SAB"};

int frame_n=0;

SemaphoreHandle_t mutex_home;

void Home::start(){

	xSemaphoreTake(mutex_home,portMAX_DELAY);

	hour=0;
	minute=0;
	second=0;
	day=1;
	month=1;
	year=2000;
	week_day = 1;

	xSemaphoreGive(mutex_home);

	display();

	this->attach_timer();
}

void Home::display(){

	String s="", s2="", s3="";
	
	xSemaphoreTake(mutex_home,portMAX_DELAY);

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

	s2=":";

	if(second<10){
		s2=s2+"0";
		s2=s2+String(second);
	}else{
		s2=s2+String(second);
	}

	s3=s3+String(week_day_name[week_day-1][0])+String(week_day_name[week_day-1][1])+String(week_day_name[week_day-1][2]);
	s3=s3+"  ";

	if(day<10){
		s3=s3+"0";
		s3=s3+String(day);
	}else{
		s3=s3+String(day);
	}

	s3=s3+"/";

	if(month<10){
		s3=s3+"0";
		s3=s3+String(month);
	}else{
		s3=s3+String(month);
	}

	s3=s3+"/"+String(year);

	xSemaphoreGive(mutex_home);

	if(frame_n==0){
		Display::clear();

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);

		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 38, s3);

		Display::drawHorizontalLine(0,51,128);
		Display::display();
	}else{
		Display::clear();

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);

		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::drawHorizontalLine(0,40,128);

		int i = 1;

		for(std::list<String>::iterator it = not_icon.begin(); it!=not_icon.end(); it++){

			if(i>109)
				break;
		
			if((*it)=="SMS"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_sms);
			}else if((*it)=="EMA"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_email);
			}else if((*it)=="FAC"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_facebook);
			}else if((*it)=="INS"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_instagram);
			}else if((*it)=="MES"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_messenger);
			}else if((*it)=="TEL"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_telephone);
			}else if((*it)=="WHA"){
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_whatsapp);
			}else{
				Display::drawXbm(i,44,LOGO_NOTIFICATION_WIDTH,LOGO_NOTIFICATION_HEIGHT,logo_geral);
			}
		
			i=i+18;
		}

		Display::display();

	}
	
}

//void Home::but_up_left(){}

//void Home::but_up_right(){}

//void Home::but_down_left(){}

//void Home::but_down_right(){}

void Home::bt_receive(char* message){

	unsigned char _hour, _minute, _second, _day, _month, _week_day;
	int _year;

	char * str, * context, delim[2];

	delim[0] = (char)0x03;
	delim[1] = '\0';

	Serial.println("ola");

	str = strtok_r(message,delim,&context);
	if(str==NULL)
		return;
	_hour=atoi(str);

	Serial.print("hour=");
	Serial.println(str);

	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	_minute=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	_second=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	_day=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	_month=atoi(str);
	
	str = strtok_r(NULL,delim,&context);
	if(str==NULL)
		return;
	_year=atoi(str);

	str = strtok_r(NULL,"\0",&context);
		if(str==NULL)
		return;
	_week_day=atoi(str);

	xSemaphoreTake(mutex_home,portMAX_DELAY);

	hour=_hour;
	minute=_minute;
	second=_second;
	day=_day;
	month=_month;
	year=_year;
	week_day=_week_day;

	xSemaphoreGive(mutex_home);

	display();
}

void Home::timer_1s(){

	unsigned char n;

	xSemaphoreTake(mutex_home,portMAX_DELAY);

	if(second==59){
		second=0;
		if (minute==59)
		{
			minute=0;
			if(hour==23){
				hour=0;
				if(week_day==7){
					week_day=1;
				}else{
					week_day++;
				}

				if(month==2){
					if( ( year % 4 == 0 && year % 100 != 0 ) || year % 400 == 0 ){
						n=29;
					}else{
						n=28;
					}
				}else{
					n = n_days_in_month[month-1];
				}

				if(day==n){
					day=0;
					if(month==12){
						month=0;
						year++;
					}else{
						month++;
					}
				}else{
					day++;
				}
			}else{
				hour++;
			}
		}else{
			minute++;
		}
	}else{
		second++;
	}

	xSemaphoreGive(mutex_home);

	if(notifying){
		if(time_of_not<NOTIFICATION_TIME){
			time_of_not++;
		}else{
			notifying=false;
		}
	}else if(curr_app()==this){
		display();
	}
}

void Home::notify(String title, String text, String icon){

	notifying = true;
	time_of_not = 0;
	
	if (frame_n == 0){
		frame_n = 1;
	}

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
	
	Display::drawHorizontalLine(0,51,128);

	Display::display();

	not_icon.push_front(icon);

	for(std::list<String>::iterator it = not_icon.begin(); it!=not_icon.end(); it++){
		if(it==not_icon.begin())
			continue;
		
		if((*it)==icon){
			not_icon.erase(it);
			break;
		}
	}

}

Home::Home(String id_in, String name_in, unsigned char* logo_in): App(id_in,name_in,logo_in) {
		mutex_home = xSemaphoreCreateMutex();
}