#include "tools.h"
#include "home.h"

timestamp show_time(){
	Home* home =(Home*) App::app_search_by_name("Home");
	timestamp t = home->show_time();

	return t;
}