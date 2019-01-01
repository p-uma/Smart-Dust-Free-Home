#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <ESP8266HTTPClient.h>
#include <DHT.h>


const char* ssid = "xxx";
const char* password = "xxx";

const int httpsPort = 443;

int measurePin = A0;
int ledPower = 12;

int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;

float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;
float temperature = 0;
float humidity = 0;
static DHT dht(2, DHT22);

float readTemperature()
{
    return dht.readTemperature();
}

float readHumidity()
{
    return dht.readHumidity();
}

float map_float(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

void blinkLED()
{
    digitalWrite(0, HIGH);
    delay(500);
    digitalWrite(0, LOW);
}

void setup(){

  pinMode(0, OUTPUT);
  Serial.begin(9600);
  pinMode(ledPower,OUTPUT);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  dht.begin();

  WiFiClientSecure client;


}
 
void loop(){
  
  digitalWrite(ledPower,LOW); // power on the LED
  delayMicroseconds(samplingTime);
 
  voMeasured = analogRead(measurePin); // read the dust value
  
  delayMicroseconds(deltaTime);
  digitalWrite(ledPower,HIGH); // turn the LED off
  delayMicroseconds(sleepTime);
 
  // 0 - 3.3V mapped to 0 - 1023 integer values
  // recover voltage
  calcVoltage = voMeasured * (3.3 / 1024);
 
  // linear eqaution taken from http://www.howmuchsnow.com/arduino/airquality/
  // Chris Nafis (c) 2012
  dustDensity = 0.17 * calcVoltage - 0.1;

  dustDensity = map_float(dustDensity, -0.1, 0.46, 0, 0.5);

  temperature = readTemperature();
  humidity = readHumidity();
 
  Serial.print("Raw Signal Value (0-1023): ");
  Serial.println(voMeasured);
 
  Serial.print("Voltage: ");
  Serial.println(calcVoltage);
 
  Serial.print("Dust Density: ");
  Serial.println(dustDensity);

  Serial.print("Temperature: ");
  Serial.println(temperature);

  Serial.print("Humidity: ");
  Serial.println(humidity);
 
  delay(500);


if(WiFi.status()== WL_CONNECTED){   //Check WiFi connection status
 
   HTTPClient http;    //Declare object of class HTTPClient
 
   http.begin("http://smarthome.nk9psnw46u.us-east-2.elasticbeanstalk.com//sendData");      //Specify request destination
   http.addHeader("Content-Type", "text/plain");  //Specify content-type header
   
   String dustDensityString = String(dustDensity);
   String temperatureString = String(temperature);
   String humidityString = String(humidity);
   String req="{\"dustDensity\":\""+dustDensityString+"\",\"temperature\":\""+temperatureString+"\", \"humidity\":\""+humidityString+"\"}";
   
   Serial.print(req); 
   
   int httpCode = http.POST(req);               //Send the request
   String payload = http.getString();           //Get the response payload
 
   Serial.println(httpCode);   //Print HTTP return code
   Serial.println(payload);    //Print request response payload

//   Serial.println("Message sent to Azure IoT Hub");
//   blinkLED();
 
   http.end();  //Close connection
 }else{
    Serial.println("Error in WiFi connection");   
 }
//  delay(1000);  //Send a request every 30 seconds
}
