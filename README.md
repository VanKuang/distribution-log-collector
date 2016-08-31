# distribution-log-collector

Logback already support send log message via JMS/Socket/SMTP.
However JMS is a bit heavy, and socket is using oio.

Netty base on nio with high performance and easy to use, obvious Netty is a good choice for such usage.

Client side:
1. Implement logback appender
2. Send log message to server via Netty

Server side:
1. Received message from clients
2. Persistent log message with well format 
3. Provide service api to GUI, like REST or dynamic streaming