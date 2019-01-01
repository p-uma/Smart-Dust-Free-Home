# Smart-Dust-Free-Home
Smart-Dust-Free-Home

## Aim:
Dust outside is tough to control but it can be regulated at least in our homes. Our
attempt through this project is to tackle the issue of air quality at home. A
smart low-cost dust monitoring system has been developed to identify, measure and expose the
amount of dust density present in the room.
There are a lot of factors which affect the volume of dust in air. Of them, temperature and humidity are considered as two of the additional parameters.

## Things and Sensors used:
• Sharp’s GP2Y1010AU0F optical dust sensor.<br />
• Sparkfun ESP8266 Thing Wifi Module for connecting and sending sensor data to
cloud.<br />
• DHT22 sensor for recording Humidity-Temperature values and analyzing them
for predicting future dust density.<br />

## Applications – What can the user do?
• Look at the real-time dust density data on a graph with date-time stamp and dust
density value axes.<br />
• Know the minimum, maximum and average dust density values recorded on
present day till current time.<br />
• Check out the previous dust density data records on a specific date in the past.<br />
• Know the current status of his Air Purifier, whether it is turned on or off.<br />
• See the prediction of dust density for seven days from the present day.<br />

## Approach:
• Built a smart dust measuring user appliance.<br />
• Connected to Microsoft Azure and AWS.<br />
• Created a cloud MySQL database for recording and maintaining current and
previous data records.<br />
• Created a responsive dashboard to showcase live, past and predict future data.<br />
• Send notifications via email and SMS to user when the average dust density
exceeds a threshold value.<br />
• Were able to predict the future dust density values using Linear Regression.<br />
• Azure is used for sending sensor data to cloud and AWS is used for WebApp
Deployment as the WebApp is developed in Java using Spring framework.<br />

## References:
1. [Microsoft Azure Tutorials – Arduino Setup](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-get-started-physical)
2. [Microsoft Azure IoT hub - Web App Setup](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-live-data-visualization-in-web-apps)
3. [ESP8266 Thing Hookup Guide](https://learn.sparkfun.com/tutorials/esp8266-thing-hookup-guide/hardware-overview)
4. [DHT22 Sensor Documentation](https://www.sparkfun.com/datasheets/Sensors/Temperature/DHT22.pdf)
5. [MySQL Database](https://www.phpmyadmin.net/docs/)
6. [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elastic-beanstalk/index.html#lang/en_us)
7. [Dashboard Bootstrap Template](https://github.com/puikinsh/nalika/tree/master/nalika)


