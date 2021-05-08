#ifndef BUZZER_H
#define BUZZER_H

#include <Arduino.h>

#define DISABLE_BUZZER 0
#define ENABLE_BUZZER 1

typedef struct buzzer_pattern{
	unsigned char * power;
	unsigned int * time;
    unsigned int * frequency;
	unsigned char size;
	unsigned char repeat;
}buzzer_pattern;

void init_buzzer();
void buzz(buzzer_pattern pattern);
void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * frequency, unsigned char size);
void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * frequency, unsigned char size, unsigned char repeat);
void buzz(unsigned char * pattern_power, unsigned int * pattern_time, unsigned int * frequency, unsigned char size, unsigned char repeat, TaskHandle_t * task_h);
void cancel_buzz(TaskHandle_t task);

int enable_buzzer(int status=-1);

#endif