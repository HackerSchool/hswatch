#include "app.h"

class Menu: public App {

	public:

		void start();
		void display();
		//void but_up_left();
		void but_up_right();
		void but_down_left();
		void but_down_right();
		//void bt_receive(char*);

		Menu(String,String,const unsigned char*);

	private:
		App** app_array;
		int n_app, index;
};