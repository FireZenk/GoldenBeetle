/*
  Golden Beetle

  This program will receive signals through bluetooth LE and
  activate motors with sent revs.

  by Jorge Garrido <firezenk@gmail.com>
*/

byte ref[2];

void setup() {
  Serial.begin(115200);  // initialize the Serial port
  pinMode(LED_BUILTIN, OUTPUT);  // initialize digital pin LED_BUILTIN as an output.
}

void loop() {
  if (Serial.available() > 2) {
    Serial.readBytes(ref, 2);
    if (byte[0] == 0 && byte[1] == 1) {
      digitalWrite(LED_BUILTIN, HIGH);
    } else {
      digitalWrite(LED_BUILTIN, LOW);
    }
  }
}
