#include <Arduino.h>
#include <list>

class App {

    public:

        const String id;
        const String name;
        const unsigned char* logo;

        virtual void start() =0;
        virtual void display();
        virtual void but_up_left();
        virtual void but_up_right();
        virtual void but_down_left();
        virtual void but_down_right();
        virtual void bt_receive(char*);

        App(String,String,unsigned char*);

        static void exit_app();
        static void run_app(String);
        static App* curr_app();

        static App* app_search_by_name(String);
        static App* app_search_by_id(String);
        static std::list<App*> app_list_show();

    private:

        static std::list<App*> app_stack;
        static std::list<App*> app_list;
};