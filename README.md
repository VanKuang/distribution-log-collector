# distribution-log-collector

Currently distribute service is popular, but will introduce one problem which is hard to locate log message under different services/machines. This project is aim to solve this problem, centralized log messsages between different services/machines. </br>

Logback already support send log message via JMS/Socket/SMTP.</br>
However JMS is a bit heavy, and socket is using oio.</br>

Netty base on nio with high performance and easy to use, obvious Netty is a good choice for such usage.

<b>Client side:</b></br>
1. Implement logback appender</br>
2. Send log message to server via Netty</br>

<b>Server side:</b></br>
1. Received message from clients</br>
2. Persistent log message with well format</br>
3. Provide service api to GUI, like REST or dynamic streaming</br>
