Java Instrumentation Agent
=================

Java agent that uses bytecode modification to print out method calls during runtime. 

Useful for application penetration testing and tracing method calls. I have used it in the past to quickly determine what methods are called on button presses via the GUI for example.

![Alt text](/main.png)

Features: 
Uses class blacklisting so you can ignore default SDK or classes you are not interested in.
The simple transformer injects into each method call and prints the method name and its parameters.
Canbe loaded at runtime or during application launch (java -javaagent:agent.jar Application.jar)

![Alt text](/main2.png)
