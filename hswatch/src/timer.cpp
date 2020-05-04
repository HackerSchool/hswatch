#include "timer.h"

void Timer::start(){
	display();
}

void Timer::display(){

	unsigned char hour,minute,second;
	String s="", s2="", s3="";
	
	xSemaphoreTake(mutex_timer,portMAX_DELAY);

	switch (state)
	{
	case chronograph:
		hour=hour_c;
		minute=minute_c;
		second=second_c;
		break;
	
	default:
		hour=hour_t;
		minute=minute_t;
		second=second_t;
		break;
	}

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
	
	xSemaphoreGive(mutex_timer);
	
	switch (state)
	{
	case chronograph:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Chronograph");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::display();
		break;
	
	case timer:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Timer");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::display();

		break;

	case timer_adjust_hours:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Timer");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::fillRect(24,40,24,2);

		Display::display();

		break;
	
	case timer_adjust_minutes:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Timer");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::fillRect(57,40,24,2);

		Display::display();

		break;

	case timer_end:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Timer");

		Display::drawHorizontalLine(0,12,128);

		if(end_blink){

			Display::setFont(arial_24);
			Display::setTextAlignment(center);
			Display::drawString(52, 14, s);
			
			Display::setFont(arial_16);
			Display::setTextAlignment(left);
			Display::drawString(84, 20, s2);
		}

		Display::display();


		break;
	
	default:

		Display::clear();
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 0, "Timer");

		Display::drawHorizontalLine(0,12,128);

		Display::setFont(arial_24);
		Display::setTextAlignment(center);
		Display::drawString(52, 14, s);
		
		Display::setFont(arial_16);
		Display::setTextAlignment(left);
		Display::drawString(84, 20, s2);

		Display::fillRect(84,40,24,2);

		Display::display();

		break;
	}
	
}

void Timer::but_up_left(){

	switch (state)
	{
	case chronograph:

		App::exit_app();
		
		break;
	
	case timer:

		state = chronograph;

		display();

		break;

	case timer_adjust_hours:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(hour_t==23){
			hour_t=0;
		}else{
			hour_t++;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;

	case timer_adjust_minutes:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(minute_t==59){
			minute_t=0;
		}else{
			minute_t++;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;

	case timer_end:
		state=timer;
		end_blink=false;
		cancel_vibration(timer_vibrator_task);
		cancel_buzz(timer_buzzer_task);
		display();
		break;

	default:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(second_t==59){
			second_t=0;
		}else{
			second_t++;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;

	}
	
}

void Timer::but_up_right(){

	switch (state)
	{
	case chronograph:

		chronograph_running = !chronograph_running;

		break;

	case timer:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(minute_t!=0||hour_t!=0||second_t!=0){
			timer_running = !timer_running;
		}

		xSemaphoreGive(mutex_timer);

		break;
	
	case timer_end:
	
		state=timer;
		end_blink=false;
		cancel_vibration(timer_vibrator_task);
		cancel_buzz(timer_buzzer_task);
		display();
		
		break;
	
	default:
		break;
	}

	
}

void Timer::but_down_left(){

	switch (state)
	{
	case chronograph:

		state = timer;

		display();

		break;

	case timer:
		break;

	case timer_adjust_hours:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(hour_t==0){
			hour_t=23;
		}else{
			hour_t--;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;

	case timer_adjust_minutes:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(minute_t==0){
			minute_t=59;
		}else{
			minute_t--;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;

	case timer_end:
		
		state=timer;
		end_blink=false;
		cancel_vibration(timer_vibrator_task);
		cancel_buzz(timer_buzzer_task);
		display();
		
		break;
	
	default:

		xSemaphoreTake(mutex_timer,portMAX_DELAY);

		if(second_t==0){
			second_t=59;
		}else{
			second_t--;
		}

		xSemaphoreGive(mutex_timer);

		display();

		break;
	}
}

void Timer::but_down_right(){

	switch (state)
	{
	case chronograph:

		if(!chronograph_running){
			xSemaphoreTake(mutex_timer,portMAX_DELAY);

			hour_c=0;
			minute_c=0;
			second_c=0;

			xSemaphoreGive(mutex_timer);

			display();
		}

		break;

	case timer:

		if(!timer_running){
			state = timer_adjust_hours;
			display();
		}

		break;

	case timer_adjust_hours:

		state = timer_adjust_minutes;
		display();

		break;

	case timer_adjust_minutes:

		state = timer_adjust_secounds;
		display();

		break;

	case timer_end:
	
		state=timer;
		end_blink=false;
		cancel_vibration(timer_vibrator_task);
		cancel_buzz(timer_buzzer_task);
		display();
	
		break;
	
	default:

		state = timer;
		display();

		break;
	}

}

void Timer::timer_1s(){

	xSemaphoreTake(mutex_timer,portMAX_DELAY);

	if(chronograph_running){

		if(second_c==59){
			second_c=0;
			if (minute_c==59)
			{
				minute_c=0;
				if(hour_c==23){
					hour_c=0;
				}else{
					hour_c++;
				}
			}else{
				minute_c++;
			}
		}else{
			second_c++;
		}
	}

	if(timer_running){

		if(second_t==0){
			second_t=59;
			if (minute_t==0)
			{
				minute_t=59;
				if(hour_t==0){
					state=timer_end;
					timer_running=false;
					minute_t=0;
					second_t=0;
					xSemaphoreGive(mutex_timer);

					vibrate(vibration_pattern_power,vibration_pattern_time,vibration_pattern_size, vibration_pattern_repeat, &timer_vibrator_task);
					buzz(buzzer_pattern_power,buzzer_pattern_time,buzzer_pattern_frequency,buzzer_pattern_size,buzzer_pattern_repeat,&timer_buzzer_task);

					if(App::curr_app()!=this){
						App::run_app("Chronograph & Timer");
					}else{
						display();
					}
					return;
				}else{
					hour_t--;
				}
			}else{
				minute_t--;
			}
		}else{
			second_t--;
		}
	}

	xSemaphoreGive(mutex_timer);

	if(App::curr_app()==this){
		switch (state)
		{
		case chronograph:
		case timer:

			if(chronograph_running||timer_running)
				display();

			break;

		case timer_end:
			end_blink=!end_blink;
			display();
			break;
		
		default:
			break;
		}
	}
}

Timer::Timer(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
	mutex_timer = xSemaphoreCreateMutex();
	
	state = chronograph;
	hour_t=0;
	minute_t=0;
	second_t=0;
	hour_c=0;
	minute_c=0;
	second_c=0;
	chronograph_running=false;
	timer_running=false;

	end_blink=false;

	this->attach_timer();
}