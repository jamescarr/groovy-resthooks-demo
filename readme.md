# REST Hooks Demo
This is a simple demo to illustrate rest hooks using ratpack as the app
server. 

## API

POST /susbcribe - creates new subscription
POST /verify - verifies subscription
POST /unsubscribe - removes subscription

GET /subscriptions - lists subscriptions

## Running It
You should use bundler to install the bundled gems (requires ruby 1.9.3
(you should really use rvm).

Run these two:

```bash
proxylocal 5055 &
proxylocal 5050 &

```
This will run proxylocal and create two addresses for you to use.

Now run the service and client service:

```bash
./run_service.sh
./run_client.sh

```

Congrats, you have a rest hooks based service running with a client
service that can create subscriptions. 


