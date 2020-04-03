#include "menu.h"
#include "display.h"
#include "home.h"
#include "logo_app.h"

void Menu::start(){

	index=0;   
	display();
}

void Menu::display(){

	String s;

	Home* home =(Home*) App::app_search_by_name("Home");
	timestamp t = home->show_time();

	if(t.hour<10){
		s=s+"0";
		s=s+String(t.hour);
	}else{
		s=s+String(t.hour);
	}

	s=s+":";

	if(t.minute<10){
		s=s+"0";
		s=s+String(t.minute);
	}else{
		s=s+String(t.minute);
	}
	
	Display::clear();

	Display::setFont(arial_10);
	Display::setTextAlignment(center);
	Display::drawString(64, 0, "Main Menu");

	Display::drawHorizontalLine(0,12,128);

	if(n_app==0){
		Display::setFont(arial_16);
		Display::setTextAlignment(center);
		Display::drawString(64, 20, "No apps");

		Display::drawHorizontalLine(0,51,128);
	
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 53, s);
	}else{
		if(index!=0){
			Display::drawXbm(0,16,LOGO_APP_WIDTH,LOGO_APP_HEIGHT,app_array[index-1]->logo);
		}

		Display::drawXbm(48,16,LOGO_APP_WIDTH,LOGO_APP_HEIGHT,app_array[index]->logo);
		Display::drawRect(46,14,38,38);
		
		Display::setFont(arial_10);
		Display::setTextAlignment(center);
		Display::drawString(64, 53, app_array[index]->name);

		if(index!=n_app-1){
			Display::drawXbm(95,16,LOGO_APP_WIDTH,LOGO_APP_HEIGHT,app_array[index+1]->logo);
		}


	}


	Display::display();
}

void Menu::but_up_right(){
	if(n_app!=0){
		App::run_app(app_array[index]->name);
	}
}

void Menu::but_down_left(){
	if(index!=0){
		index--;
		display();
	}else{
		App::exit_app();
	}
}

void Menu::but_down_right(){
	if((index!=n_app-1)&&(n_app!=0)){
		index++;
		display();
	}
}

Menu::Menu(String id_in, String name_in, const unsigned char* logo_in): App(id_in,name_in,logo_in) {
	
	int j=0;
	n_app=0;

	for(std::list<App*>::iterator i = App::app_list_show().begin(); i!=App::app_list_show().end() ; i++){
		if((*i)->logo!=NULL){
			n_app++;
		}
	}

	app_array = (App**) malloc(sizeof(App*)*n_app);

	for(std::list<App*>::iterator i = App::app_list_show().begin(); i!=App::app_list_show().end() ; i++){
		if((*i)->logo!=NULL){
			app_array[j]=*i;
			j++;
		}
	}
}