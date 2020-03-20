#include <Arduino.h>
#include "communication.h"
#include "home.h"
#include "logo_hs.h"
#include <Wire.h>
#include <SSD1306Wire.h>
#include "queue_display.h"
#include "display.h"
#include "notification.h"

#define B_LEFT_UP 18
#define B_LEFT_DOWN 16
#define B_RIGHT_UP 19
#define B_RIGHT_DOWN 17

#define BT_BUFFER_SIZE 300

TaskHandle_t bt_task_h, timer_task_h;

QueueHandle_t * queue_display;

hw_timer_t * timer = NULL;

SSD1306Wire screen(0x3c, SDA, SCL);

void bt_task(void*);
int init_display();
void IRAM_ATTR onTimer();
void timer_task(void*);

void setup() {

	Serial.begin(9600);

	pinMode(B_LEFT_UP, INPUT_PULLUP);
	pinMode(B_LEFT_DOWN, INPUT_PULLUP);
	pinMode(B_RIGHT_UP, INPUT_PULLUP);
	pinMode(B_RIGHT_DOWN, INPUT_PULLUP);

	init_bluetooth("HSWatch");
	init_display();

	queue_display = new QueueHandle_t();

	*queue_display = xQueueCreate(15,sizeof(msg_queue_display));

	new Home("TIM","Home",NULL);
	new Notification("NOT","Notification",NULL);

	App::run_app("Home");

	xTaskCreate(bt_task,"bluetooth task",8192,NULL,1,&bt_task_h);
	xTaskCreate(timer_task,"timer task",8192,NULL,1,&timer_task_h);

	timer = timerBegin(0,80,true);
	timerAttachInterrupt(timer, &onTimer, true);
	timerAlarmWrite(timer, 1000000, true);
	timerAlarmEnable(timer);

}

void loop() {

	msg_queue_display msg;

	xQueueReceive(*queue_display, &msg, portMAX_DELAY);
	
	switch (msg.type)
	{
	case _clear:
		screen.clear();
		break;

	case _display:
		screen.display();
		break;

	case _drawHorizontalLine:
		screen.drawHorizontalLine(msg.a,msg.b,msg.c);
		break;
	
	case _drawString:
		screen.drawString(msg.a,msg.b,String(msg.s));
		break;

	case _drawStringMaxWidth:
		screen.drawStringMaxWidth(msg.a,msg.b,msg.c,String(msg.s));
		break;

	case _setTextAlignment:
		if(msg.a==0){
			screen.setTextAlignment(TEXT_ALIGN_LEFT);
		}else if(msg.a==1){
			screen.setTextAlignment(TEXT_ALIGN_CENTER);
		}else if(msg.a==2){
			screen.setTextAlignment(TEXT_ALIGN_RIGHT);
		}else{
			screen.setTextAlignment(TEXT_ALIGN_CENTER_BOTH);
		}
		break;

	case _drawXbm:
		screen.drawXbm(msg.a, msg.b, msg.c, msg.d, msg.image);
	
	default:
		if(msg.a==0){
			screen.setFont(ArialMT_Plain_10);
		}else if(msg.a==1){
			screen.setFont(ArialMT_Plain_16);
		}else{
			screen.setFont(ArialMT_Plain_24);
		}
		break;
	}
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

int init_display(){
	 
	screen.init();

	screen.flipScreenVertically();

	screen.clear();
	screen.drawXbm(
		(128  - LOGO_WIDTH ) / 2,
		(64 - LOGO_HEIGHT) / 2,
		LOGO_WIDTH, LOGO_HEIGHT, logo_hs);
	screen.display();
	delay(3000);

	return 0;
}

void IRAM_ATTR onTimer(){
	vTaskNotifyGiveFromISR(timer_task_h,NULL);
}

void timer_task(void*){
	while (1)
	{
		ulTaskNotifyTake(pdFALSE, portMAX_DELAY);
		App::call_timer();
	}
}