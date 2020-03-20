#include "app.h"

std::list<App*> App::app_stack;
std::list<App*> App::app_list;
std::list<App*> App::timer_attached_app;

void App::display(){}

void App::but_up_left(){
	exit_app();
}

void App::but_up_right(){}

void App::but_down_left(){}

void App::but_down_right(){}

void App::bt_receive(char*){}

void App::timer_1s(){}

App::App(String id_in, String name_in, unsigned char* logo_in): id(id_in), name(name_in), logo(logo_in){
	app_list.push_back(this);
}

void App::exit_app(){
	if(app_stack.size()>1){
		app_stack.pop_front();
		curr_app()->display();
	}
}

void App::run_app(String name_in){
	App* a = app_search_by_name(name_in);
	app_stack.push_front(a);
	a->start();
}

App* App::curr_app(){

	return app_stack.front();

}

App* App::app_search_by_name(String name_in){

	for(std::list<App*>::iterator it = app_list.begin(); it!=app_list.end(); it++){
		if((*it)->name==name_in){
			return *it;
		}
	}
	return NULL;

}

App* App::app_search_by_id(String id_in){

	for(std::list<App*>::iterator it = app_list.begin(); it!=app_list.end(); it++){
		if((*it)->id==id_in){
			return *it;
		}
	}
	return NULL;

}

std::list<App*> App::app_list_show(){
	return app_list;
}

void App::attach_timer(){
	timer_attached_app.push_back(this);
}

void App::detach_timer(){
	timer_attached_app.remove(this);
}

void App::call_timer(){
	for(std::list<App*>::iterator it = timer_attached_app.begin(); it!=timer_attached_app.end(); it++){
		(*it)->timer_1s();
	}
}