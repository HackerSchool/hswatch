#include "app.h"

class Home: public App {

    public:

        void start();
        void display();
        //void but_up_left();
        //void but_up_right();
        //void but_down_left();
        //void but_down_right();
        void bt_receive(char*);

        Home(String,String,unsigned char*);

    private:

        unsigned char hour,minute,second,day,month,week_day;
        unsigned int year;
};