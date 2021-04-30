#include "app.h"
#include "tools.h"

#define N_COLOR 4

class FlashLight: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();

		FlashLight(String,String,const unsigned char*);

	private:

		enum State {page1, page2, page3} state;

		TaskHandle_t led_task=NULL;

		unsigned char led_color[N_COLOR][3] = {{255,255,255},{255,0,0},{0,255,0},{0,0,255}};
		String color_name[N_COLOR] = {"White","Red","Green","Blue"};

		float gain;
		int color;
		int led_status;

};