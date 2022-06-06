#include "app.h"
#include "tools.h"

class Quotes: public App {

	public:

		void start();
		void display();
		void but_up_left();
		void but_up_right();
		void but_down_left();
		void bt_receive(char*);


		Quotes(String,String,const unsigned char*);

	private:

		enum State {loading, page1, page2} state;
		String quote_text, quote_title;


};