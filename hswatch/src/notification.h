#include "app.h"

class Notification: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();
		//void but_down_right();
		void bt_receive(char*);

		Notification(String,String,unsigned char*);

	private:

		typedef struct notification{
			String *logo;
			unsigned char hour, minute;
			String *title, *text;
		} notification;
		
		std::list<notification> notification_list;
		std::list<notification>::iterator index;
		unsigned char notification_number;
};