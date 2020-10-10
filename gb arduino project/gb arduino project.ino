/*
  Golden Beetle

  This program will receive signals through bluetooth LE and
  activate motors with sent revs.

  by Jorge Garrido <firezenk@gmail.com>
*/

void setup() {
  Serial.begin(115200);  //initialize the Serial port
  pinMode(LED_BUILTIN, OUTPUT);  // initialize digital pin LED_BUILTIN as an output.
}

void loop() {
  if (Serial.available())  {
    char inChar = (char)Serial.read();
    if (inChar == 'ON') {
      digitalWrite(LED_BUILTIN, HIGH);
    } else {
      digitalWrite(LED_BUILTIN, LOW);
    }
  }
}
