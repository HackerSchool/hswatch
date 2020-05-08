#include "led.h"

#define PWM_FREQ 5000
#define PWM_CHANNEL_R 3
#define PWM_CHANNEL_G 4
#define PWM_CHANNEL_B 5
#define PWM_RESOLUTION 8
#define PWM_GPIO_R 14
#define PWM_GPIO_G 13
#define PWM_GPIO_B 12

SemaphoreHandle_t mutex_led;

void blink_task(void* par_in);

void init_led(){
	ledcSetup(PWM_CHANNEL_R,PWM_FREQ,PWM_RESOLUTION);
	ledcSetup(PWM_CHANNEL_G,PWM_FREQ,PWM_RESOLUTION);
	ledcSetup(PWM_CHANNEL_B,PWM_FREQ,PWM_RESOLUTION);
	ledcAttachPin(PWM_GPIO_R,PWM_CHANNEL_R);
	ledcAttachPin(PWM_GPIO_G,PWM_CHANNEL_G);
	ledcAttachPin(PWM_GPIO_B,PWM_CHANNEL_B);
	mutex_led = xSemaphoreCreateMutex();
}

void blink_led(led_pattern p, TaskHandle_t * task_h){
	
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

	xTaskCreate(blink_task,"blink task",8192,pattern,1,task_h);
}

void blink_task(void* par_in){

	led_pattern * pattern = (led_pattern*) par_in;
	int j=0;

	xSemaphoreTake(mutex_led,portMAX_DELAY);
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

	vTaskDelete(NULL);
}

void cancel_blink_led(TaskHandle_t task){
	if(task==NULL)
		return;
	xTaskNotifyGive(task);
}