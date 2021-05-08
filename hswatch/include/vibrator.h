#ifndef VIBRATOR_H
#define VIBRATOR_H

#include <Arduino.h>

#define DISABLE_VIBRATOR 0
#define ENABLE_VIBRATOR 1

typedef struct vibrate_pattern{
	unsigned char * power;
	unsigned int * time;
	unsigned char size;
	unsigned char repeat;
}vibrate_pattern;

void init_vibrator();
void vibrate(vibrate_pattern pattern);
void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size);
void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size, unsigned char repeat);
void vibrate(unsigned char * pattern_power, unsigned int * pattern_time, unsigned char size, unsigned char repeat, TaskHandle_t * task_h);
void cancel_vibration(TaskHandle_t task);

int enable_vibrator(int status=-1);

#endif