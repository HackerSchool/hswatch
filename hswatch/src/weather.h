#include "app.h"
#include "tools.h"

class Weather: public App {

	public:

		void start();
		void display();
		void but_down_left();
		void but_down_right();
		void bt_receive(char*);

		Weather(String,String,const unsigned char*);

	private:

		const unsigned char* icon_converter(int icon);

		enum State {loading, page1, page2} state;
		SemaphoreHandle_t mutex_weather;
		bool available;
		String location;

		typedef struct weather{
			String max_temp, min_temp;
			String rain;
			int icon; 
		}weather;

		weather forecast[6]; 

};