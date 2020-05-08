#include "weather.h"
#include "logo_weather.h"

void Weather::start(){

	char buf[5] = "WEA\0";

	send_bt(buf, 5);

	if(state!=loading)
		state=page1;

	display();

}

void Weather::display(){

	unsigned char n;

	xSemaphoreTake(mutex_weather,portMAX_DELAY);

	switch (state)
	{
	case loading:
		
		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Weather");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 25, "Loading");

		Display::drawHorizontalLine(0,51,128);

		Display::display();

		break;
	
	default:

		if(state==page1){
			n = 0;
		}else if(state==page2){
			n = 2;
		}else{
			n = 4;
		}

		timestamp t = show_time();

		Display::clear();

		Display::setFont(arial_10);
		Display::setTextAlignment(center);

		Display::drawString(64, 0, location);

		Display::drawHorizontalLine(0,12,128);

		Display::setTextAlignment(left);
		Display::drawString(15, 15, week_day_name[(t.week_day+n-1)%7]);
		Display::drawString(84, 15, week_day_name[(t.week_day+n)%7]);

		Display::drawXbm(0,35,LOGO_WEATHER_WIDTH,LOGO_WEATHER_HEIGHT,icon_converter(forecast[n].icon));
		Display::drawXbm(68,35,LOGO_WEATHER_WIDTH,LOGO_WEATHER_HEIGHT,icon_converter(forecast[n+1].icon));
		
		Display::setFont(arial_10);
		
		Display::drawString(27, 30, forecast[n].max_temp + "C");
		Display::drawString(95, 30, forecast[n+1].max_temp + "C");

		Display::drawString(27, 41, forecast[n].min_temp + "C");
		Display::drawString(95, 41, forecast[n+1].min_temp + "C");

		Display::drawString(33, 52, forecast[n].rain + "%");
		Display::drawString(101, 52, forecast[n+1].rain + "%");

		Display::display();

		break;
	}

	xSemaphoreGive(mutex_weather);
	
}

void Weather::but_down_left(){

	switch (state)
	{
	case page2:
		state=page1;
		display();
		break;
	
	case page3:
		state=page2;
		display();
		break;
	
	default:
		break;
	}

}

void Weather::but_down_right(){

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

void Weather::bt_receive(char* message){

	char * str, * context, delim[2];

	delim[0] = (char)0x03;
	delim[1] = '\0';

	xSemaphoreTake(mutex_weather,portMAX_DELAY);

	str = strtok_r(message,delim,&context);
	if(str==NULL){
		xSemaphoreGive(mutex_weather);
		return;
	}

	Serial.println(str);

	location = String(str);

	for(int i=0; i<6;i++){

		str = strtok_r(NULL,delim,&context);
		if(str==NULL)
			break;
		Serial.println(str);
		forecast[i].icon=atoi(str);

		str = strtok_r(NULL,delim,&context);
		if(str==NULL)
			break;
		Serial.println(str);
		forecast[i].max_temp=String(str);
		
		str = strtok_r(NULL,delim,&context);
		if(str==NULL)
			break;
		Serial.println(str);
		forecast[i].min_temp=String(str);

		if(i!=5)
			str = strtok_r(NULL,delim,&context);
		else
			str = strtok_r(NULL,"\0",&context);
		
		if(str==NULL)
			break;
		Serial.println(str);
		forecast[i].rain=String(str);

	}

	xSemaphoreGive(mutex_weather);

	if(state==loading)
		state=page1;
	
	if(App::curr_app()==this)
		display();	
}

const unsigned char* Weather::icon_converter(int icon){
	if(icon==800){
		return sun_icon;
	}else if(icon==801||icon==802||icon==803){
		return clouds1_icon;
	}else if(icon==804){
		return clouds2_icon;
	}else if(icon==300||icon==301||icon==302||icon==500){
		return rain1_icon;
	}else if(icon==501||icon==502||icon==511||icon==520||icon==521||icon==522){
		return rain2_icon;
	}else if(icon==600||icon==601||icon==602||icon==610||icon==611||icon==612||icon==621||icon==622||icon==623){
		return snow_icon;
	}else if(icon==700||icon==711||icon==721||icon==731||icon==741||icon==751){
		return fog_icon;
	}else if(icon==200||icon==201||icon==202||icon==230||icon==231||icon==232||icon==233){
		return thunder_icon;
	}else{
		return rain2_icon;
	}
}

Weather::Weather(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
		mutex_weather = xSemaphoreCreateMutex();

		state = loading;
		available = false;

}