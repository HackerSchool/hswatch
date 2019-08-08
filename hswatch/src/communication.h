#include <Arduino.h>
#include "BluetoothSerial.h"

void init_bluetooth(String bt_name);
int receive_bt(char * buf, int size);
void send_bt(char * buffer, int size);