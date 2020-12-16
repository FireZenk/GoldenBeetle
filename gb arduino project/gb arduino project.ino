/*
  Golden Beetle

  This program will receive signals through bluetooth LE and
  activate motors with sent revs.

  by Jorge Garrido <firezenk@gmail.com>
*/

#include <AFMotor.h>
#include <SoftwareSerial.h>

AF_DCMotor motor3(3);
AF_DCMotor motor4(4);

SoftwareSerial BT1(A5, A3); //A5 and A3

String command;

void setup() {
  motor3.run(FORWARD);
  motor4.run(FORWARD);
  motor3.setSpeed(0);
  motor4.setSpeed(0);

  Serial.begin(9600);
  Serial.println("Boop beep!");
  BT1.begin(9600);
}

void loop() {
  if (BT1.available()) {
    char command = BT1.read();
    Serial.println(command);

    switch(command) {
      case '0':
        motor3.run(FORWARD);
        motor4.run(FORWARD);
        motor3.setSpeed(250);
        motor4.setSpeed(250);
        break;
       case '1':
        motor3.run(BACKWARD);
        motor4.run(BACKWARD);
        motor3.setSpeed(250);
        motor4.setSpeed(250);
        break;
       case '2':
        motor3.run(FORWARD);
        motor4.run(FORWARD);
        motor3.setSpeed(0);
        motor4.setSpeed(250);
        break;
       case '3':
        motor3.run(FORWARD);
        motor4.run(FORWARD);
        motor3.setSpeed(250);
        motor4.setSpeed(0);
        break;
       case '4':
        motor3.run(FORWARD);
        motor4.run(FORWARD);
        motor3.setSpeed(0);
        motor4.setSpeed(0);
        break;
    }
  }
}
