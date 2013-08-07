# REST Hooks Demo
This is a simple demo to illustrate rest hooks using ratpack as the app
server. 

## API
Here are the following endpoints supported by the service/ratpack.groovy service. 

* POST /susbcribe - creates new subscription
* POST /confirm - verifies subscription
* POST /unsubscribe - removes subscription

* GET /subscriptions - lists subscriptions

## Running It
You should use bundler to install the bundled gems (requires ruby 1.9.3
(you should really use rvm).

Run these two:

```bash
proxylocal 5055 &
proxylocal 5050 &

```
This will run proxylocal and create two addresses for you to use. You
should get some output like this:

```bash

jamescarr@James-MacBook-Air: ~
$ proxylocal 5050 &    
Local server on port 5050 is now publicly available via:                                                                                                  [1:23:09]
http://pu5x.t.proxylocal.com/


jamescarr@James-MacBook-Air: ~
$ proxylocal 5055 &                                                                                                                                           [1:23:11]
Local server on port 5055 is now publicly available via:
http://hd9u.t.proxylocal.com/
```

Take note of those urls!

Now run the service and client service and pass the urls for the two
earlier proxylocal commands (yours will be different!):

```bash
./run_service.sh http://hd9u.t.proxylocal.com/
./run_client.sh http://pu5x.t.proxylocal.com/

```

Congrats, you have a rest hooks based service running with a client
service that can create subscriptions. 


