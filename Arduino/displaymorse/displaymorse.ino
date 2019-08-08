#include "Morsea.h"

void setup() {
  Morse dm(3);
  Morsea dm1(3);
  Morse* morse = &dm;
  Morse* morse1 = &dm1;
  morse->dot();
  morse->dash();
  morse->dot();
  morse1->dot();
  morse1->dash();
  morse1->dot();

}

void loop() {
  // put your main code here, to run repeatedly:

}
