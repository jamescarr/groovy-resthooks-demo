@GrabResolver("https://oss.jfrog.org/artifactory/repo")
@Grab("org.ratpack-framework:ratpack-groovy:0.9.0-SNAPSHOT")
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='0.8.0')
import wslite.rest.*
import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.ratpackframework.groovy.templating.Template.groovyTemplate
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.security.MessageDigest
import java.util.Random
import java.math.BigInteger

// You can change anything in the ratpack {} closure without needing to restart
def subscriptions = new Subscriptions()
def employees = []

ratpack {

    handlers {
        get { 
            render groovyTemplate("add_employee.html", employees: employees)
        }

        post("add") {
            employee = [name: request.form?.name, email:request.form?.email]
            employees <<  employee
            render groovyTemplate("add_employee.html", 
              employees: employees,
              message: "${request.form.name} has been added!")
    
            println "Notifying all subscribers!"
            subscriptions.notify('employee_added', employee)
        }

        post("api/unsubscribe") { 
            def slurper = new JsonSlurper()
            def unsubRequest = slurper.parseText(request.text)
            unsubRequest.url = unsubRequest.subscription_url
            println "api/unsubscribe recieved ${request.text}"
            
            if (subscriptions.exists(unsubRequest)) {
                subscriptions.remove(unsubRequest)
                response.send "application.json", '{"message": "Subscription Removed!"}'
            } else {
                def message = "No subscription for ${unsubRequest.url} and token ${unsubRequest.token}."
                response.status 404
                response.send "application/json", "{\"message\": \"${message}\"}"
            }
        }
        
        get("api/me") {
            // always authenticate.
            response.send "application/json", "{\"account_id\": 128319283}"
        }

        post("api/subscribe") { 
            def slurper = new JsonSlurper()
            def data = slurper.parseText(request.text)
            
            println "api/subscribe recieved ${request.text}"
            subscriptions.add(data)

            response.send "application/json", '{"status": "Ok."}'

        }

        // list all subscriptions
        get("api/subscriptions") {
            JsonBuilder json = new JsonBuilder()
            json.subscriptions {
              results subscriptions.all()
            }
            response.send "application/json", json.toString()
        }

        assets "public"
    }
}

class Subscription {
    String status
    String url
    String token
    String event

    private static final Random random = new Random()

    def notify(event, data) {
        if (this.status != 'active') return;

        def client = new RESTClient(url)
        client.post() {
            type "application/json"
            json data: data, type: event
        }
    }
}

class Subscriptions {
    private def subscriptions = []

    def exists(details) {
        return subscriptions.findResult { 
            details.url == it.url && details.token == it.token ? it : null
        }
    }
  
    def remove(subscription) {
        subscriptions.removeAll {
            it.url == subscription.url && it.token == subscription.token 
        }
    }

    def add(data) {
        def subscription = new Subscription(
            url:data.subscription_url,
            event: data.event,
            status: 'active'
        )
        this.subscriptions << subscription
    }

    def notify(event, data) {
        subscriptions.each { 
            if (it.event == event) {
              it.notify(event, data)
            }
        }
    }

    def all() {
        subscriptions
    }
}


