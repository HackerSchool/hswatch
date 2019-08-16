#include "home.h"
#include "display.h"

void Home::start(){
    hour=0;
    minute=0;
    second=0;
    day=1;
    month=1;
    year=2000;
    week_day = 1;

    display();
}

void Home::display(){

    String s="";

    Display::clear();

    Display::drawHorizontalLine(0,12,128);

    Display::setFont(arial_24);
    Display::setTextAlignment(center);
    
    if(hour<10){
        s=s+"0";
        s=s+String(hour);
    }else{
        s=s+String(hour);
    }

    s=s+":";

    if(minute<10){
        s=s+"0";
        s=s+String(minute);
    }else{
        s=s+String(minute);
    }

    Display::drawString(64, 14, s);
    Display::drawHorizontalLine(0,51,128);
    Display::display();
    
}

//void Home::but_up_left(){}

//void Home::but_up_right(){}

//void Home::but_down_left(){}

//void Home::but_down_right(){}

void Home::bt_receive(char* message){

    char * str, * context, delim[2];

    delim[0] = (char)0x03;
    delim[1] = '\0';

    Serial.println("ola");

    str = strtok_r(message,delim,&context);
    if(str==NULL)
        return;
    hour=atoi(str);

    Serial.print("hour=");
    Serial.println(str);

    str = strtok_r(NULL,delim,&context);
    if(str==NULL)
        return;
    minute=atoi(str);
    
    str = strtok_r(NULL,delim,&context);
    if(str==NULL)
        return;
    second=atoi(str);
    
    str = strtok_r(NULL,delim,&context);
    if(str==NULL)
        return;
    day=atoi(str);
    
    str = strtok_r(NULL,delim,&context);
    if(str==NULL)
        return;
    month=atoi(str);
    
    str = strtok_r(NULL,delim,&context);
    if(str==NULL)
        return;
    year=atoi(str);

    str = strtok_r(NULL,"\0",&context);
        if(str==NULL)
        return;
    week_day=atoi(str);

    display();
}

Home::Home(String id_in, String name_in, unsigned char* logo_in): App(id_in,name_in,logo_in) {}