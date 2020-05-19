#ifndef BATTERY_H
#define BATTERY_H

#include <Arduino.h>

void init_battery();
void check_level_timer();
void reset_battery();

int percentage_battery();
int status_battery();
bool charging_battery();

#endif