#include "buzzer.h"

#define PWM_FREQ 2000
#define PWM_CHANNEL 1
#define PWM_RESOLUTION 8
#define PWM_GPIO 33

SemaphoreHandle_t mutex_buzzer;

void buzz_task(void* par_in);

void init_buzzer(){
	ledcSetup(PWM_CHANNEL,PWM_FREQ,PWM_RESOLUTION);
	ledcAttachPin(PWM_GPIO,PWM_CHANNEL);
	mutex_buzzer = xSemaphoreCreateMutex();
}

void buzz(buzzer_pattern pattern){
	buzz(pattern.power, pattern.time, pattern.frequency, pattern.size, pattern.repeat);
}

void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * pattern_frequency, unsigned char size){
	buzz(pattern_power, pattern_time, pattern_frequency, size, 1);
}

void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * pattern_frequency, unsigned char size, unsigned char repeat){

	TaskHandle_t task_h;

	buzz(pattern_power, pattern_time, pattern_frequency, size, repeat, &task_h);
}

void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * pattern_frequency, unsigned char size, unsigned char repeat, TaskHandle_t * task_h){
	
	buzzer_pattern * pattern =(buzzer_pattern*) malloc(sizeof(buzzer_pattern));
	pattern->power = (unsigned char*) malloc(sizeof(unsigned char)*size);
	pattern->time = (unsigned int*) malloc(sizeof(int)*size);
    pattern->frequency = (unsigned int*) malloc(sizeof(int)*size);

	memcpy(pattern->power,pattern_power, size);
	memcpy(pattern->time,pattern_time, size*sizeof(int));
    memcpy(pattern->frequency,pattern_frequency, size*sizeof(int));
	pattern->size = size;
	pattern->repeat = repeat;

	xTaskCreate(buzz_task,"buzz task",8192,pattern,1,task_h);
}

void buzz_task(void* par_in){

	buzzer_pattern * pattern = (buzzer_pattern*) par_in;

	xSemaphoreTake(mutex_buzzer,portMAX_DELAY);
	for(int j=0; j<pattern->repeat; j++){
		for(int i=0; i<pattern->size; i++){
			if(ulTaskNotifyTake(pdTRUE,0)==1){
				ledcWrite(PWM_CHANNEL,0);
				xSemaphoreGive(mutex_buzzer);

				free(pattern->power);
				free(pattern->time);
                free(pattern->frequency);
				free(pattern);

				vTaskDelete(NULL);
			}
            ledcWriteTone(PWM_CHANNEL, pattern->frequency[i]);
			ledcWrite(PWM_CHANNEL,pattern->power[i]);
			delay(pattern->time[i]);
		}
	}
	ledcWrite(PWM_CHANNEL,0);

	xSemaphoreGive(mutex_buzzer);

	free(pattern->power);
	free(pattern->time);
    free(pattern->frequency);
	free(pattern);

	vTaskDelete(NULL);
}

void cancel_buzz(TaskHandle_t task){
	xTaskNotifyGive(task);
}