#include "app.h"
#include "tools.h"

class Alarm: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();
		void but_down_right();
		void timer_1s();

		Alarm(String,String,const unsigned char*);

	private:

		enum State {alarm1_home, alarm1_hours, alarm1_minutes, alarm2_home, alarm2_hours, alarm2_minutes, alarm3_home, alarm3_hours, alarm3_minutes} state;
		timestamp alarm1_timestamp, alarm2_timestamp, alarm3_timestamp, timer_timestamp;
		SemaphoreHandle_t mutex_alarm1, mutex_alarm2, mutex_alarm3;
		bool blink, alarm1, alarm2, alarm3, alarm1_ringing, alarm2_ringing, alarm3_ringing;
		bool alarm_off();

		unsigned char vibration_pattern_power[2] = {255,0};
		unsigned int vibration_pattern_time[2] = {2000,1000};
		unsigned char vibration_pattern_size = 2;
		unsigned char vibration_pattern_repeat = 5;

		TaskHandle_t alarm_vibrator_task = NULL;

		unsigned char buzzer_pattern_power[8] = {255,0,255,0,255,0,255,0};
		unsigned int buzzer_pattern_time[8] = {500,500,500,500,500,500,500,1500};
		unsigned int buzzer_pattern_frequency[8] = {2000,2000,2000,2000,2000,2000,2000,2000};
		unsigned char buzzer_pattern_size = 8;
		unsigned char buzzer_pattern_repeat = 3;

		TaskHandle_t alarm_buzzer_task = NULL;
};