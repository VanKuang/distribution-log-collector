# distribution-log-collector

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
