#ifndef LED_H
#define LED_H

#include <Arduino.h>

#define DISABLE_LED 0
#define ENABLE_LOW_PRIORITY_LED 1
#define ENABLE_HIGH_PRIORITY_LED 2
#define ENABLE_LED 3

typedef struct led_pattern{
	unsigned char * r;
	unsigned char * g;
	unsigned char * b;
	unsigned int * time;
	unsigned int size;
	unsigned char repeat;
}led_pattern;

void init_led();
void rainbow_led(TaskHandle_t * task_h, int priority=1);
void fade_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority=1);
void fade2_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority=1);
void fade3_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority=1);
void turnon_led(unsigned char r, unsigned char g, unsigned char b, TaskHandle_t * task_h, int priority=1);
void blink_led(led_pattern pattern, TaskHandle_t * task_h, int priority=1);
void cancel_blink_led(TaskHandle_t task);

int enable_led(int status=-1);

#endif