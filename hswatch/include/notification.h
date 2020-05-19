#include "app.h"
#include "tools.h"

#define NOTIFICATION_TIME 10

class Notification: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();
		void but_down_right();
		void bt_receive(char*);
		void timer_1s();

		Notification(String,String,const unsigned char*);

	private:

		typedef struct notification{
			String *logo;
			unsigned char hour, minute;
			String *title, *text;
		} notification;
		
		std::list<notification> notification_list;
		std::list<notification>::iterator index;
		unsigned char notification_number;

		bool notifying, led_blink;
		unsigned char time_of_not;

		TaskHandle_t led_task=NULL;

		unsigned char vibration_pattern_power[3] = {255,0,255};
		unsigned int vibration_pattern_time[3] = {500,500,500};
		unsigned char vibration_pattern_size = 3;
		unsigned char vibration_pattern_repeat = 1;

		TaskHandle_t vibrator_task=NULL;

		/*unsigned char pattern_r[2] = {0, 0};
		unsigned char pattern_g[2] = {0, 255};
		unsigned char pattern_b[2] = {255,255};
		unsigned int led_pattern_time[2] = {2000,2000};
		unsigned int led_pattern_size = 2;
		unsigned char led_pattern_repeat = 255;

		led_pattern pattern = {
			.r=pattern_r,
			.g=pattern_g,
			.b=pattern_b,
			.time=led_pattern_time,
			.size=led_pattern_size,
			.repeat=led_pattern_repeat
		};*/
};