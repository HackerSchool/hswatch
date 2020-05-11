#include "app.h"

std::list<App*> App::app_stack;
std::list<App*> App::app_list;
std::list<App*> App::timer_attached_app;

SemaphoreHandle_t mutex_app;
TaskHandle_t task_h;

void App::display(){}

void App::but_up_left(){
	exit_app();
}

void App::but_up_right(){}

void App::but_down_left(){}

void App::but_down_right(){}

void App::bt_receive(char*){}

void App::timer_1s(){}

void App::exit(){}

App::App(String id_in, String name_in, const unsigned char* logo_in): id(id_in), name(name_in), logo(logo_in){
	app_list.push_back(this);
}

void App::exit_app(){

	curr_app()->exit();

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
	xSemaphoreTake(mutex_app,portMAX_DELAY);
	timer_attached_app.push_back(this);
	timer_attached_app.unique();
	xSemaphoreGive(mutex_app);
}

void App::detach_timer(){
	xTaskCreate(detach_timer_task,"detach timer task",8192,this,1,&task_h);
}

void App::call_timer(){
	xSemaphoreTake(mutex_app,portMAX_DELAY);
	for(std::list<App*>::iterator it = timer_attached_app.begin(); it!=timer_attached_app.end(); it++){
		(*it)->timer_1s();
	}
	xSemaphoreGive(mutex_app);
}

void App::init_app(){
	mutex_app = xSemaphoreCreateMutex();
}

void App::detach_timer_task(void* par){

	App* a = (App*) par;

	xSemaphoreTake(mutex_app,portMAX_DELAY);
	timer_attached_app.remove(a);
	xSemaphoreGive(mutex_app);
	
	vTaskDelete(NULL);
}