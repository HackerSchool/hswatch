#include <SoftwareSerial.h>
SoftwareSerial BT(2,3);

void setup() {
  BT.begin(9600);

}

void loop() {

  char incoming;

  if(BT.available()){
    incoming=BT.read();
    BT.println(incoming);
  }
  
}
