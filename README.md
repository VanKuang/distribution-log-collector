# distribution-log-collector

Base on logback, implement Appender to send log message to log collector server, via Netty.
At log collector server side, will received message from many clients, and persistent those message will well format, 
and provide service api to GUI, like REST or dynamic streaming.
