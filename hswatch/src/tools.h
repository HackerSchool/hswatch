#ifndef TOOLS_H
#define TOOLS_H

#include "display.h"
#include "communication.h"
#include "vibrator.h"
#include "buzzer.h"

typedef struct timestamp{
	unsigned char hour,minute,second,day,month,week_day;
	unsigned int year;
}timestamp;

timestamp show_time();

#endif