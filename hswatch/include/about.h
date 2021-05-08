#include "app.h"
#include "tools.h"


class About: public App {

	public:

		void start();
		void display();
		void but_up_left();

		About(String,String,const unsigned char*);

	private:

		TaskHandle_t led_task=NULL;

};