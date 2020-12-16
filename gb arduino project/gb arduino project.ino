/*
  Golden Beetle

  This program will receive signals through bluetooth LE and
  activate motors with sent revs.

  by Jorge Garrido <firezenk@gmail.com>
*/

#include <AFMotor.h>

AF_DCMotor motor3(3);
AF_DCMotor motor4(4);

byte ref[1];

const int buzzer = 2; // buzzer at digital pin D2
const int extLED = 5; // led at digital pin D5

void setup() {
  Serial.begin(115200);  // initialize the Serial port
  //pinMode(LED_BUILTIN, OUTPUT);  // initialize digital pin LED_BUILTIN as an output.

  //pinMode(buzzer, OUTPUT); // Set buzzer - pin 9 as an output

  Serial.begin(9600); //Puerto bt

  motor3.setSpeed(255);
  motor4.setSpeed(10);
}

void loop() {
  if (Serial.available()) {
    Serial.readBytes(ref, 1);

    switch(ref[0]) {
      case 0x00:
        digitalWrite(LED_BUILTIN, LOW); // INTERNAL LED LOW
        break;
      case 0x01:
        digitalWrite(LED_BUILTIN, HIGH); // INTERNAL LED HIGH
        break;
      case 0x02:
        tone(buzzer, 1000); // BEEP
        delay(1000);
        noTone(buzzer);
        break;
      case 0x03:
        digitalWrite(extLED, HIGH); // EXTERNAL LED BLINK
        delay(1000);
        digitalWrite(extLED, LOW);
        break;
    }

    Serial.print(ref[0], DEC);
  }

  motor3.run(FORWARD);
  motor4.run(FORWARD);
}
