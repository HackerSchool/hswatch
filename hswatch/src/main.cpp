#include <Arduino.h>
#include "communication.h"
#include "home.h"
#include "display.h"

#define B_LEFT_UP 18
#define B_LEFT_DOWN 16
#define B_RIGHT_UP 19
#define B_RIGHT_DOWN 17

#define BT_BUFFER_SIZE 300

TaskHandle_t bt_task_h;

void bt_task(void*);

void setup() {

  Serial.begin(9600);

  pinMode(B_LEFT_UP, INPUT_PULLUP);
  pinMode(B_LEFT_DOWN, INPUT_PULLUP);
  pinMode(B_RIGHT_UP, INPUT_PULLUP);
  pinMode(B_RIGHT_DOWN, INPUT_PULLUP);

  init_bluetooth("HSWatch");
  init_display();

  new Home("TIM","Home",NULL);

  App::run_app("Home");

  xTaskCreate(bt_task,"bluetooth task",8192,NULL,1,&bt_task_h);
}

void loop() {
  //App::app_search_by_id("TIM")->display();
  //init_display();
  screen->clear();
  screen->display();
  delay(1000);
}

void bt_task(void* par_in){

  //Adafruit_SSD1306 * screen=(Adafruit_SSD1306 *)par_in;
  //init_display();
  Serial.println(App::app_search_by_id("TIM")->id);
  //App::app_search_by_id("TIM")->display();
  //screen->clear();
  //screen->display();
  //App::run_app("Home");
  Serial.println("ola");
  int len;
  char buf[BT_BUFFER_SIZE], aux[BT_BUFFER_SIZE]; 
  char *context, *m_id, delim[2]; 
  App* app_to_call;
  
  delim[0] = (char)0x03;
  delim[1] = '\0';

  while(1){
    len = receive_bt(buf,BT_BUFFER_SIZE);

    buf[len]='\0';
    Serial.print("buf=");
    Serial.println(buf);

    if(len<5)
      continue;

    strcpy(aux,buf+4);
    m_id = strtok_r(buf,delim,&context);

    if(m_id==NULL)
      continue;
    
    app_to_call = App::app_search_by_id(String(m_id));
    if(app_to_call!=NULL){
      app_to_call->bt_receive(aux);
    }
  }

}