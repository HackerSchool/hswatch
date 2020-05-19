#include "battery.h"

#define N_SAMPLES 5
#define SAMPLING_PERIOD 5
#define BATTERY_V_RANGE 900
#define CHANGER 5

#define BATTERY_PIN 34
#define STANDBY_PIN 4
#define CHARGING_PIN 15

int full_level = 3900;
int level[N_SAMPLES] = {0};
int percentage = 100;
int status = 3;
int time_counter = 0;
int vector_iterator = 0; 
int changer=CHANGER;
int availble_samples=0;

void init_battery(){
	pinMode(STANDBY_PIN, INPUT_PULLUP);
	pinMode(CHARGING_PIN, INPUT_PULLUP);

	attachInterrupt(STANDBY_PIN, reset_battery, FALLING);
}

void check_level_timer(){

	int l=0;
	int s;

	if(time_counter==0){

		if(availble_samples<N_SAMPLES){
			availble_samples++;
		}

		level[vector_iterator] = analogRead(BATTERY_PIN);

		for(int i=0; i<availble_samples;i++)
			l = l + level[i];

		l = l/availble_samples;
		
		percentage = (l-full_level+BATTERY_V_RANGE)*100/BATTERY_V_RANGE;

		Serial.println(percentage);

		if(percentage>100){
			percentage=100;
		}else if(percentage<0){
			percentage=0;
		}

		if(percentage>=70){
			s=3;
		}else if(percentage>=45){
			s=2;
		}else if(percentage>=20){
			s=1;
		}else{
			s=0;
		}

		if(s!=status){
			if(changer==CHANGER){
				status = s;
				changer = 0;
			}else{
				changer++;
			}
		}else{
			changer = 0;
		}

		if(vector_iterator<N_SAMPLES-1)
			vector_iterator++;
		else
			vector_iterator=0;
		
		time_counter = SAMPLING_PERIOD;
	}else{
		time_counter--;
	}
}

void reset_battery(){
	full_level = analogRead(BATTERY_PIN);
	Serial.print("change");
	Serial.println(full_level);
}

int percentage_battery(){
	return percentage;
}

int status_battery(){
	return status;
}

bool charging_battery(){
	return !digitalRead(CHARGING_PIN);
}