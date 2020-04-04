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
		timestamp end_timestamp;
};