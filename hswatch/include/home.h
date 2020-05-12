#include "app.h"
#include "tools.h"

class Home: public App {

	public:

		void start();
		void display();
		void but_up_left();
		//void but_up_right();
		void but_down_left();
		void but_down_right();
		void bt_receive(char*);
		void timer_1s();

		void notify(String, String, String);
		void delete_notification(String);
		timestamp show_time();

		bool alarm1_en, alarm2_en, alarm3_en;

		Home(String,String,const unsigned char*);

	private:

		unsigned char hour,minute,second,day,month,week_day;
		unsigned int year;
		SemaphoreHandle_t mutex_home;
		std::list<String> not_icon;
};