#include "BluetoothSerial.h"
#include "communication.h"

BluetoothSerial bt;

void init_bluetooth(String bt_name){
	bt.begin(bt_name); //Bluetooth device name
	bt.setTimeout(0xFFFFFFFF);
}

int receive_bt(char * buf, int size){
	return bt.readBytesUntil('\0',buf,size-1);
}

void send_bt(char * buffer, int size){
	if(bt.hasClient())
		while(size>0)
			size = size - bt.write((uint8_t *)buffer, size);
}