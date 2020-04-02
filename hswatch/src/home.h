#include "app.h"

#define NOTIFICATION_TIME 10

class Home: public App {

	public:

		void start();
		void display();
		void but_up_left();
		//void but_up_right();
		void but_down_left();
		//void but_down_right();
		void bt_receive(char*);
		void timer_1s();

		void notify(String, String, String);
		void delete_notification(String);

		Home(String,String,unsigned char*);

	private:

		unsigned char hour,minute,second,day,month,week_day;
		unsigned int year;
		SemaphoreHandle_t mutex_home;
		std::list<String> not_icon;
		bool notifying = false;
		unsigned char time_of_not;
};