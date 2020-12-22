#ifndef BATTERY_H
#define BATTERY_H

#include <Arduino.h>

void init_battery();
void check_level_timer();

int percentage_battery();
int status_battery();

#endif