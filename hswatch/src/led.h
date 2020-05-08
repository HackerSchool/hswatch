#ifndef LED_H
#define LED_H

#include <Arduino.h>

typedef struct led_pattern{
	unsigned char * r;
	unsigned char * g;
	unsigned char * b;
	unsigned int * time;
	unsigned char size;
	unsigned char repeat;
}led_pattern;

void init_led();
void blink_led(led_pattern pattern, TaskHandle_t * task_h);
void cancel_blink_led(TaskHandle_t task);

#endif