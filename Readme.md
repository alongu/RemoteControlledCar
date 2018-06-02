Remote Controlled Car

1. Raspberry PI is trying to create a connection to a WiFi automatically over my phone - set by the HotSpot.
HotSpot's user name and password is located in /etc/network/interface - in configuration file.
2. Once connected, it opens a socket for listening to incoming tcp connections, over: 192.168.43.155:9999.
3. Android application it trying to connect to PI using a TcpClient. and sends data regarding the location of the phone. 
