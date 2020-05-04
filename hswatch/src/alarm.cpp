#include "alarm.h"
#include "home.h"

void Alarm::start(){
	display();
}

void Alarm::display(){

	bool alarm_ringing;
	bool en_alarm;

	unsigned char hour,minute;
	String s="";

        
    Display::clear();
		
	Display::setFont(arial_10);
	Display::setTextAlignment(center);

	xSemaphoreTake(mutex_alarm1,portMAX_DELAY);
    xSemaphoreTake(mutex_alarm2,portMAX_DELAY);
    xSemaphoreTake(mutex_alarm3,portMAX_DELAY);

	switch (state)
	{
	case alarm1_home:
    case alarm1_hours:
    case alarm1_minutes:
		hour=alarm1_timestamp.hour;
		minute=alarm1_timestamp.minute;
		alarm_ringing=alarm1_ringing;
		en_alarm=alarm1;
        Display::drawString(64, 0, "Alarm 1");
		break;
    
    case alarm2_home:
    case alarm2_hours:
    case alarm2_minutes:
		hour=alarm2_timestamp.hour;
		minute=alarm2_timestamp.minute;
		alarm_ringing=alarm2_ringing;
		en_alarm=alarm2;
        Display::drawString(64, 0, "Alarm 2");
		break;
	
	default:
		hour=alarm3_timestamp.hour;
		minute=alarm3_timestamp.minute;
		alarm_ringing=alarm3_ringing;
		en_alarm=alarm3;
        Display::drawString(64, 0, "Alarm 3");
		break;
	}

    xSemaphoreGive(mutex_alarm3);
    xSemaphoreGive(mutex_alarm2);
    xSemaphoreGive(mutex_alarm1);

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

	if(alarm_ringing){
		if(blink){
			Display::setFont(arial_24);
			Display::setTextAlignment(center);
			Display::drawString(64, 14, s);
		}
	}else{
		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(64, 14, s);
	}
	
	switch (state)
	{
	case alarm1_hours:
	case alarm2_hours:
	case alarm3_hours:

		Display::fillRect(36,40,24,2);
		break;
	
	case alarm1_minutes:
	case alarm2_minutes:
	case alarm3_minutes:

		Display::fillRect(69,40,24,2);
		break;
	
	default:

		break;
	}

	if(en_alarm){
		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 42, "ON");
	}else{
		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 42, "OFF");
	}

	Display::display();
}

void Alarm::but_up_left(){

	if(alarm_off()){
		return;
	}

	switch (state)
	{
	case alarm1_home:

		App::exit_app();
		
		break;
	
	case alarm2_home:

		state = alarm1_home;

		display();

		break;

	case alarm3_home:

		state = alarm2_home;

		display();

		break;

	case alarm1_hours:

		xSemaphoreTake(mutex_alarm1,portMAX_DELAY);

		if(alarm1_timestamp.hour==23){
			alarm1_timestamp.hour=0;
		}else{
			alarm1_timestamp.hour++;
		}

		xSemaphoreGive(mutex_alarm1);

		display();

		break;

	case alarm2_hours:

		xSemaphoreTake(mutex_alarm2,portMAX_DELAY);

		if(alarm2_timestamp.hour==23){
			alarm2_timestamp.hour=0;
		}else{
			alarm2_timestamp.hour++;
		}

		xSemaphoreGive(mutex_alarm2);

		display();

		break;

	case alarm3_hours:

		xSemaphoreTake(mutex_alarm3,portMAX_DELAY);

		if(alarm3_timestamp.hour==23){
			alarm3_timestamp.hour=0;
		}else{
			alarm3_timestamp.hour++;
		}

		xSemaphoreGive(mutex_alarm3);

		display();

		break;

	case alarm1_minutes:

		xSemaphoreTake(mutex_alarm1,portMAX_DELAY);

		if(alarm1_timestamp.minute==59){
			alarm1_timestamp.minute=0;
		}else{
			alarm1_timestamp.minute++;
		}

		xSemaphoreGive(mutex_alarm1);

		display();

		break;

	case alarm2_minutes:

		xSemaphoreTake(mutex_alarm2,portMAX_DELAY);

		if(alarm2_timestamp.minute==59){
			alarm2_timestamp.minute=0;
		}else{
			alarm2_timestamp.minute++;
		}

		xSemaphoreGive(mutex_alarm2);

		display();

		break;

	case alarm3_minutes:

		xSemaphoreTake(mutex_alarm3,portMAX_DELAY);

		if(alarm3_timestamp.minute==59){
			alarm3_timestamp.minute=0;
		}else{
			alarm3_timestamp.minute++;
		}

		xSemaphoreGive(mutex_alarm3);

		display();

		break;

	default:

		break;

	}
	
}

void Alarm::but_up_right(){

	Home* home = (Home*) App::app_search_by_name("Home");

	if(alarm_off()){
		return;
	}

	switch (state)
	{
	case alarm1_home:

		alarm1 = !alarm1;

		home->alarm1_en=!home->alarm1_en;
		display();

		break;
	
	case alarm2_home:

		alarm2 = !alarm2;

		home->alarm2_en=!home->alarm2_en;
		display();

		break;

	case alarm3_home:

		alarm3 = !alarm3;

		home->alarm3_en=!home->alarm3_en;
		display();

		break;
	
	default:
		break;
	}

	
}

void Alarm::but_down_left(){

	if(alarm_off()){
		return;
	}

	switch (state)
	{
	case alarm1_home:

		state = alarm2_home;

		display();

		break;

	case alarm2_home:

		state = alarm3_home;

		display();

		break;

	case alarm3_home:
		break;

	case alarm1_hours:

		xSemaphoreTake(mutex_alarm1,portMAX_DELAY);

		if(alarm1_timestamp.hour==0){
			alarm1_timestamp.hour=23;
		}else{
			alarm1_timestamp.hour--;
		}

		xSemaphoreGive(mutex_alarm1);

		display();

		break;

	case alarm2_hours:

		xSemaphoreTake(mutex_alarm2,portMAX_DELAY);

		if(alarm2_timestamp.hour==0){
			alarm2_timestamp.hour=23;
		}else{
			alarm2_timestamp.hour--;
		}

		xSemaphoreGive(mutex_alarm2);

		display();

		break;

	case alarm3_hours:

		xSemaphoreTake(mutex_alarm3,portMAX_DELAY);

		if(alarm3_timestamp.hour==0){
			alarm3_timestamp.hour=23;
		}else{
			alarm3_timestamp.hour--;
		}

		xSemaphoreGive(mutex_alarm3);

		display();

		break;

	case alarm1_minutes:

		xSemaphoreTake(mutex_alarm1,portMAX_DELAY);

		if(alarm1_timestamp.minute==0){
			alarm1_timestamp.minute=59;
		}else{
			alarm1_timestamp.minute--;
		}

		xSemaphoreGive(mutex_alarm1);

		display();

		break;

	case alarm2_minutes:

		xSemaphoreTake(mutex_alarm2,portMAX_DELAY);

		if(alarm2_timestamp.minute==0){
			alarm2_timestamp.minute=59;
		}else{
			alarm2_timestamp.minute--;
		}

		xSemaphoreGive(mutex_alarm2);

		display();

		break;

	case alarm3_minutes:

		xSemaphoreTake(mutex_alarm3,portMAX_DELAY);

		if(alarm3_timestamp.minute==0){
			alarm3_timestamp.minute=59;
		}else{
			alarm3_timestamp.minute--;
		}

		xSemaphoreGive(mutex_alarm3);

		display();

		break;
	
	default:
		break;
	}
}

void Alarm::but_down_right(){

	if(alarm_off()){
		return;
	}

	switch (state)
	{
	case alarm1_home:

		state = alarm1_hours;
		display();

		break;

	case alarm1_hours:

		state = alarm1_minutes;
		display();

		break;

	case alarm1_minutes:

		state = alarm1_home;
		display();

		break;

	case alarm2_home:

		state = alarm2_hours;
		display();

		break;

	case alarm2_hours:

		state = alarm2_minutes;
		display();

		break;

	case alarm2_minutes:

		state = alarm2_home;
		display();

		break;

	case alarm3_home:

		state = alarm3_hours;
		display();

		break;

	case alarm3_hours:

		state = alarm3_minutes;
		display();

		break;

	case alarm3_minutes:

		state = alarm3_home;
		display();

		break;
	
	default:

		break;
	}

}

void Alarm::timer_1s(){

	timestamp t = show_time();

	bool enable_alarm=false;

	if(!(timer_timestamp.hour==t.hour&&timer_timestamp.minute==t.minute)){

		timer_timestamp.hour=t.hour;
		timer_timestamp.minute=t.minute;
		
		if(alarm1&&(App::curr_app()!=this||(state!=alarm1_hours&&state!=alarm1_minutes))){
			if(alarm1_timestamp.hour==t.hour&&alarm1_timestamp.minute==t.minute){
				
				alarm1_ringing=true;
				state=alarm1_home;

				enable_alarm=true;
			}
		}

		if(alarm2&&(App::curr_app()!=this||(state!=alarm2_hours&&state!=alarm2_minutes))){
			if(alarm2_timestamp.hour==t.hour&&alarm2_timestamp.minute==t.minute){
				
				alarm2_ringing=true;
				state=alarm2_home;

				enable_alarm=true;
			}
		}

		if(alarm3&&(App::curr_app()!=this||(state!=alarm3_hours&&state!=alarm3_minutes))){
			if(alarm3_timestamp.hour==t.hour&&alarm3_timestamp.minute==t.minute){
				
				alarm3_ringing=true;
				state=alarm3_home;

				enable_alarm=true;
			}
		}

		if(enable_alarm){
			//cancel previous vibration and buzzer patterns
			cancel_vibration(alarm_vibrator_task);
			cancel_buzz(alarm_buzzer_task);

			//start buzzer and vibration pattern
			vibrate(vibration_pattern_power,vibration_pattern_time,vibration_pattern_size, vibration_pattern_repeat, &alarm_vibrator_task);
			buzz(buzzer_pattern_power,buzzer_pattern_time,buzzer_pattern_frequency,buzzer_pattern_size,buzzer_pattern_repeat,&alarm_buzzer_task);
		
			if(App::curr_app()!=this){
				App::run_app("Alarm");
			}else{
				display();
			}
		}

	}

	if(alarm1_ringing||alarm2_ringing||alarm3_ringing){
		blink=!blink;
		if(App::curr_app()==this)
			display();
	}
}

bool Alarm::alarm_off(){
	switch (state)
	{
	case alarm1_home:
	case alarm1_hours:
	case alarm1_minutes:
		if(alarm1_ringing){
			alarm1_ringing=false;
			cancel_vibration(alarm_vibrator_task);
			cancel_buzz(alarm_buzzer_task);
			display();
			return true;
		}else{
			return false;
		}
		break;
	
	case alarm2_home:
	case alarm2_hours:
	case alarm2_minutes:
		if(alarm2_ringing){
			alarm2_ringing=false;
			cancel_vibration(alarm_vibrator_task);
			cancel_buzz(alarm_buzzer_task);
			display();
			return true;
		}else{
			return false;
		}

		break;
	
	default:
		if(alarm3_ringing){
			alarm3_ringing=false;
			cancel_vibration(alarm_vibrator_task);
			cancel_buzz(alarm_buzzer_task);
			display();
			return true;
		}else{
			return false;
		}
		break;
	}
}

Alarm::Alarm(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
	mutex_alarm1 = xSemaphoreCreateMutex();
	mutex_alarm2 = xSemaphoreCreateMutex();
	mutex_alarm3 = xSemaphoreCreateMutex();
	
	alarm1_timestamp.hour=0;
	alarm1_timestamp.minute=0;
	alarm2_timestamp.hour=0;
	alarm2_timestamp.minute=0;
	alarm3_timestamp.hour=0;
	alarm3_timestamp.minute=0;
	timer_timestamp.hour=24;
	timer_timestamp.minute=0;

	state=alarm1_home;

	blink=false;

	alarm1=false;
	alarm2=false;
	alarm3=false;
	alarm1_ringing=false;
	alarm2_ringing=false;
	alarm3_ringing=false;

	this->attach_timer();
}