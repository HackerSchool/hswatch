#include "app.h"
#include "tools.h"

class Timer: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();
		void but_down_right();
		void timer_1s();

		Timer(String,String,const unsigned char*);

	private:

		enum State {chronograph, timer, timer_adjust_hours, timer_adjust_minutes, timer_adjust_secounds, timer_end} state;
		unsigned char hour_c,minute_c,second_c;
		unsigned char hour_t,minute_t,second_t;
		SemaphoreHandle_t mutex_timer;
		bool end_blink, chronograph_running, timer_running;

		unsigned char vibration_pattern_power[2] = {255,0};
		unsigned int vibration_pattern_time[2] = {2000,1000};
		unsigned char vibration_pattern_size = 2;
		unsigned char vibration_pattern_repeat = 5;

		TaskHandle_t timer_vibrator_task;

		unsigned char buzzer_pattern_power[8] = {255,0,255,0,255,0,255,0};
		unsigned int buzzer_pattern_time[8] = {500,500,500,500,500,500,500,1500};
		unsigned int buzzer_pattern_frequency[8] = {2000,2000,2000,2000,2000,2000,2000,2000};
		unsigned char buzzer_pattern_size = 8;
		unsigned char buzzer_pattern_repeat = 3;

		TaskHandle_t timer_buzzer_task;
};