#include "vibrator.h"
#include <esp32-hal-ledc.h>

#define PWM_FREQ 5000
#define PWM_CHANNEL 0
#define PWM_RESOLUTION 8
#define PWM_GPIO 32

SemaphoreHandle_t mutex_vibrator;

void vibrate_task(void* par_in);

void init_vibrator(){
	ledcSetup(PWM_CHANNEL,PWM_FREQ,PWM_RESOLUTION);
	ledcAttachPin(PWM_GPIO,PWM_CHANNEL);
	mutex_vibrator = xSemaphoreCreateMutex();
}

void vibrate(vibrate_pattern pattern){
	vibrate(pattern.power, pattern.time, pattern.size, pattern.repeat);
}

void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size){
	vibrate(pattern_power, pattern_time, size, 1);
}

void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size, unsigned char repeat){

	TaskHandle_t task_h;

	vibrate(pattern_power, pattern_time, size, repeat, &task_h);
}

void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size, unsigned char repeat, TaskHandle_t * task_h){
	
	vibrate_pattern * pattern =(vibrate_pattern*) malloc(sizeof(vibrate_pattern));
	pattern->power = (unsigned char*) malloc(sizeof(unsigned char)*size);
	pattern->time = (unsigned int*) malloc(sizeof(int)*size);

	memcpy(pattern->power,pattern_power, size);
	memcpy(pattern->time,pattern_time, size*sizeof(int));
	pattern->size = size;
	pattern->repeat = repeat;

	xTaskCreate(vibrate_task,"vibrate task",8192,pattern,1,task_h);
}

void vibrate_task(void* par_in){

	vibrate_pattern * pattern = (vibrate_pattern*) par_in;

	xSemaphoreTake(mutex_vibrator,portMAX_DELAY);
	for(int j=0; j<pattern->repeat; j++){
		for(int i=0; i<pattern->size; i++){
			if(ulTaskNotifyTake(pdTRUE,0)==1){
				ledcWrite(PWM_CHANNEL,0);
				xSemaphoreGive(mutex_vibrator);

				free(pattern->power);
				free(pattern->time);
				free(pattern);

				vTaskDelete(NULL);
			}
			ledcWrite(PWM_CHANNEL,pattern->power[i]);
			delay(pattern->time[i]);
		}
	}
	ledcWrite(PWM_CHANNEL,0);

	xSemaphoreGive(mutex_vibrator);

	free(pattern->power);
	free(pattern->time);
	free(pattern);

	vTaskDelete(NULL);
}

void cancel_vibration(TaskHandle_t task){
	xTaskNotifyGive(task);
}