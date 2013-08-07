@GrabResolver("https://oss.jfrog.org/artifactory/repo")
@Grab("org.ratpack-framework:ratpack-groovy:0.9.0-SNAPSHOT")
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='0.8.0')
import wslite.rest.*
import static org.ratpackframework.groovy.RatpackScript.ratpack
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.security.MessageDigest
import java.util.Random
import java.math.BigInteger

// You can change anything in the ratpack {} closure without needing to restart
def subscriptions = new Subscriptions()

ratpack {

    handlers {
        post("unsubscribe") { 
          
        }
        post("confirm") {
          def slurper = new JsonSlurper()
          def confirmation = slurper.parseText(request.text)
          
          if (subscriptions.verify(confirmation)) {
            response.send "application.json", '{"message": "Subscription Confirmed!"}'
          } else {
            response.status 401
            response.send "application.json", '{"message": "Invalid Token"}'

          }
          
        }

        post("subscribe") { 
          def slurper = new JsonSlurper()
          def data = slurper.parseText(request.text)

          subscriptions.add(data)

          response.send "application/json", '{"status": "Ok."}'

        }

        // list all subscriptions
        get("subscriptions") {
            JsonBuilder json = new JsonBuilder()
            json.subscriptions {
              results subscriptions.all()
            }
            response.send "application/json", json.toString()
        }

    }
}

class Subscription {
  String status
  String url
  String token
  String event

  private static final Random random = new Random()

  def requestValidation() {
    this.token = this.generateSha1Hash()
    def client = new RESTClient(this.url)
    def BASE_URL = System.env['DEMO_SERVICE_BASE_URL']
    
    client.post() {
      type "application/json"
      json confirm_url: "${BASE_URL}/confirm", token: this.token, type: 'confirmation_request'
    }
  }

  private def generateSha1Hash() {
    def messageDigest = MessageDigest.getInstance("SHA1")
    def nextInt = this.random.nextInt(1000000)
    messageDigest.update("${this.url}${nextInt}".bytes)
    return new BigInteger(1, messageDigest.digest()).toString(16).padLeft( 40, '0' )
  }
}

class Subscriptions {
  private def subscriptions = []
  
  def add(data) {
    def subscription = new Subscription(
      url:data.url,
      event: data.event,
      status: 'pending'
    )
    subscription.requestValidation()
    this.subscriptions << subscription
     
  }
  
  def all() {
    subscriptions
  }

  def verify(confirmation) {
    def subscription = subscriptions.findResult { 
        confirmation.token.equals(it.token) ? it : null
    }

    if (subscription) {
      subscription.status = 'active'
      return true
    }
    return false
  }

}


