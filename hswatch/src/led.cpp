#include "led.h"

#define PWM_FREQ 5000
#define PWM_CHANNEL_R 3
#define PWM_CHANNEL_G 4
#define PWM_CHANNEL_B 5
#define PWM_RESOLUTION 8
#define PWM_GPIO_R 25
#define PWM_GPIO_G 26
#define PWM_GPIO_B 27

SemaphoreHandle_t mutex_led, mutex_change_context;

TaskHandle_t *led_task;

bool enable_l, enable_priority;

bool approve_blink(int priority);

void create_blink_task(led_pattern * p, TaskHandle_t * task_h, int priority);

void blink_task(void* par_in);

void init_led(){
	ledcSetup(PWM_CHANNEL_R,PWM_FREQ,PWM_RESOLUTION);
	ledcSetup(PWM_CHANNEL_G,PWM_FREQ,PWM_RESOLUTION);
	ledcSetup(PWM_CHANNEL_B,PWM_FREQ,PWM_RESOLUTION);
	ledcAttachPin(PWM_GPIO_R,PWM_CHANNEL_R);
	ledcAttachPin(PWM_GPIO_G,PWM_CHANNEL_G);
	ledcAttachPin(PWM_GPIO_B,PWM_CHANNEL_B);
	mutex_led = xSemaphoreCreateMutex();
	mutex_change_context = xSemaphoreCreateMutex();

	enable_l=true;
	enable_priority=true;
}

void rainbow_led(TaskHandle_t * task_h, int priority){

	if(!approve_blink(priority))
		return;

	led_pattern * p =(led_pattern*) malloc(sizeof(led_pattern));

	p->size=156;
	p->repeat=255;

	p->r = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->g = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->b = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->time = (unsigned int*) malloc(sizeof(int)*p->size);
	int t=0;

	for(int r=255; r>=0; r=r-5){
		p->r[t]=r;
		p->g[t]=255-r;
		p->b[t]=0;
		p->time[t]=100;
		t++;
	}

	for(int g=255; g>=0; g=g-5){
		p->r[t]=0;
		p->g[t]=g;
		p->b[t]=255-g;
		p->time[t]=100;
		t++;
	}

	for(int b=255; b>=0; b=b-5){
		p->r[t]=255-b;
		p->g[t]=0;
		p->b[t]=b;
		p->time[t]=100;
		t++;
	}

	create_blink_task(p,task_h,priority);
}

void fade_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority){
	
	if(!approve_blink(priority))
		return;

	led_pattern * p =(led_pattern*) malloc(sizeof(led_pattern));

	p->size=103;
	p->repeat=255;

	p->r = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->g = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->b = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->time = (unsigned int*) malloc(sizeof(int)*p->size);
	
	int t=0;

	for(float m=0; m<=1; m=m+0.02){
		p->r[t]=r*m;
		p->g[t]=g*m;
		p->b[t]=b*m;
		p->time[t]=50;
		t++;
	}
	
	for(float m=1; m>=0; m=m-0.02){
		p->r[t]=r*m;
		p->g[t]=g*m;
		p->b[t]=b*m;
		p->time[t]=50;
		t++;
	}

	p->r[t]=0;
	p->g[t]=0;
	p->b[t]=0;
	p->time[t]=5000;

	create_blink_task(p,task_h,priority);	
}

void fade2_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority){
	
	if(!approve_blink(priority))
		return;

	led_pattern * p =(led_pattern*) malloc(sizeof(led_pattern));

	p->size=103;
	p->repeat=255;

	p->r = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->g = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->b = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->time = (unsigned int*) malloc(sizeof(int)*p->size);
	
	int t=0;
	float aux;

	for(float m=0; m<=1; m=m+0.02){
		aux=m*m;
		p->r[t]=r*aux;
		p->g[t]=g*aux;
		p->b[t]=b*aux;
		p->time[t]=50;
		t++;
	}
	
	for(float m=1; m>=0; m=m-0.02){
		aux=m*m;
		p->r[t]=r*aux;
		p->g[t]=g*aux;
		p->b[t]=b*aux;
		p->time[t]=50;
		t++;
	}

	p->r[t]=0;
	p->g[t]=0;
	p->b[t]=0;
	p->time[t]=5000;

	create_blink_task(p,task_h,priority);	
}

void fade3_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority){

	if(!approve_blink(priority))
		return;

	led_pattern * p =(led_pattern*) malloc(sizeof(led_pattern));

	p->size=103;
	p->repeat=255;

	p->r = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->g = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->b = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->time = (unsigned int*) malloc(sizeof(int)*p->size);
	
	int t=0;
	float aux;

	for(float m=0; m<=1; m=m+0.02){
		aux=m*m*m;
		p->r[t]=r*aux;
		p->g[t]=g*aux;
		p->b[t]=b*aux;
		p->time[t]=50;
		t++;
	}
	
	for(float m=1; m>=0; m=m-0.02){
		aux=m*m*m;
		p->r[t]=r*aux;
		p->g[t]=g*aux;
		p->b[t]=b*aux;
		p->time[t]=50;
		t++;
	}

	p->r[t]=0;
	p->g[t]=0;
	p->b[t]=0;
	p->time[t]=5000;

	create_blink_task(p,task_h,priority);	
}

void turnon_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority){

	if(!approve_blink(priority))
		return;
	
	led_pattern * p =(led_pattern*) malloc(sizeof(led_pattern));

	Serial.println("turnon_led");

	p->size=1;
	p->repeat=255;

	p->r = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->g = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->b = (unsigned char*) malloc(sizeof(unsigned char)*p->size);
	p->time = (unsigned int*) malloc(sizeof(int)*p->size);

	p->r[0]=r;
	p->g[0]=g;
	p->b[0]=b;
	p->time[0]=500;

	Serial.println("turnon_led2");

	create_blink_task(p,task_h,priority);	
}

void blink_led(led_pattern p, TaskHandle_t * task_h, int priority){

	if(!approve_blink(priority))
		return;
	
	led_pattern * pattern =(led_pattern*) malloc(sizeof(led_pattern));
	pattern->r = (unsigned char*) malloc(sizeof(unsigned char)*p.size);
	pattern->g = (unsigned char*) malloc(sizeof(unsigned char)*p.size);
	pattern->b = (unsigned char*) malloc(sizeof(unsigned char)*p.size);
	pattern->time = (unsigned int*) malloc(sizeof(int)*p.size);

	memcpy(pattern->r,p.r, p.size);
	memcpy(pattern->g,p.g, p.size);
	memcpy(pattern->b,p.b, p.size);
	memcpy(pattern->time,p.time, p.size*sizeof(int));
	pattern->size = p.size;
	pattern->repeat = p.repeat;

	create_blink_task(pattern,task_h,priority);
}

int enable_led(int status){
	
	if(status==0){
		enable_l=false;
		enable_priority=false;
	}else if(status==1){
		enable_l=true;
		enable_priority=false;
	}else if(status==2){
		enable_l=false;
		enable_priority=true;
	}else if(status==3){
		enable_l=true;
		enable_priority=true;
	}

	if((enable_priority==0)&&(enable_l==0)){
		return 0;
	}else if((enable_priority==0)&&(enable_l==1)){
		return 1;
	}else if((enable_priority==1)&&(enable_l==0)){
		return 2;
	}else{
		return 3;
	}
}

void blink_task(void* par_in){
	xSemaphoreTake(mutex_led, portMAX_DELAY);

	led_pattern * pattern = (led_pattern*) par_in;
	int j=0;

	while( j<pattern->repeat){
		for(int i=0; i<pattern->size; i++){
			if(ulTaskNotifyTake(pdTRUE,0)==1){
				ledcWrite(PWM_CHANNEL_R,0);
				ledcWrite(PWM_CHANNEL_G,0);
				ledcWrite(PWM_CHANNEL_B,0);
				xSemaphoreGive(mutex_led);

				free(pattern->r);
				free(pattern->g);
				free(pattern->b);
				free(pattern->time);
				free(pattern);
				xSemaphoreGive(mutex_led);

				*led_task=NULL;
				led_task=NULL;

				Serial.println("delete_task");

				vTaskDelete(NULL);
			}
			ledcWrite(PWM_CHANNEL_R,pattern->r[i]);
			ledcWrite(PWM_CHANNEL_G,pattern->g[i]);
			ledcWrite(PWM_CHANNEL_B,pattern->b[i]);
			delay(pattern->time[i]);
		}
		if(pattern->repeat!=255){
			j++;
		}
	}
	ledcWrite(PWM_CHANNEL_R,0);
	ledcWrite(PWM_CHANNEL_G,0);
	ledcWrite(PWM_CHANNEL_B,0);
	xSemaphoreGive(mutex_led);

	free(pattern->r);
	free(pattern->g);
	free(pattern->b);
	free(pattern->time);
	free(pattern);

	*led_task=NULL;
	led_task=NULL;
	vTaskDelete(NULL);
}

bool approve_blink(int priority){
	if(priority==1){
		if(enable_priority){
			return true;
		}else{
			return false;
		}
	}else{
		if(enable_l){
			return true;
		}else{
			return false;
		}
	}
}

void create_blink_task(led_pattern * p, TaskHandle_t * task_h, int priority){

	Serial.println("blink_task");
	
	xSemaphoreTake(mutex_change_context,portMAX_DELAY);

	if(priority==1){

		Serial.println("blink_task priority");
		
		if(led_task!=NULL)
			cancel_blink_led(*led_task);

		Serial.println("blink_task priority2");

		led_task=task_h;

		xTaskCreate(blink_task,"blink task",8192,p,1,task_h);
	}else{
		
		if(uxSemaphoreGetCount(mutex_led)==1){
			led_task=task_h;

			xTaskCreate(blink_task,"blink task",8192,p,1,task_h);
		}else{

			free(p->r);
			free(p->g);
			free(p->b);
			free(p->time);
			free(p);
		}
	}

	xSemaphoreGive(mutex_change_context);
}

void cancel_blink_led(TaskHandle_t task){

	Serial.println("cancel 1");

	if(task==NULL)
		return;
	xTaskNotifyGive(task);

	Serial.println("cancel 2");

	while(uxSemaphoreGetCount(mutex_led)!=1){
	}

	Serial.println("cancel 3");
}