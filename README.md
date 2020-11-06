# Networked Multithreaded Chat (Server & Client)
This weeks challenge was to create a chat server between server and client! I would have preferred to use SSL encryption, but this included me setting up my own keystores etc though this could be an extension. It is multithreaded in order to deal with multiple clients at once.

## Usage
I have implemented this chat using `System.console.readLine();`, so the programs will not run in environments such as IDEs.

### Server
In the command prompt, use `java ChatServer [PORT_NUMBER]` to start the server and listen on the specific port. To increase the chance of using a free port, ports are only allowed in range 1024-65535.

Once the server has started, it will remain active until closed forcibly such as closing the command prompt. It will display any messages and statuses whilst running.

### Client
In the command prompt, use `java ChatClient [HOSTNAME] [PORT_NUMBER] [USERNAME]` to start the client and try to connect to the port on the host. A list of available commands will be displayed, currently only `/exit`, `/e`, `/refresh` & `/r`. Typing a message will send it to the server and allow other clients to read it. Refreshing will occur automatically when you send a message, but you can manually refresh using the `/r` command.
