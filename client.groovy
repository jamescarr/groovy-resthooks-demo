@GrabResolver("https://oss.jfrog.org/artifactory/repo")
@Grab("org.ratpack-framework:ratpack-groovy:0.9.0-SNAPSHOT")
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='0.8.0')
import static org.ratpackframework.groovy.RatpackScript.ratpack
import wslite.rest.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.security.MessageDigest
import java.util.Random
import java.math.BigInteger


def BASE_URL = System.env['DEMO_CLIENT_BASE_URL']

ratpack {
    handlers {
        post("recieve/:hash") { 
            def slurper = new JsonSlurper()
            def data = slurper.parseText(request.text)
            println request.text
            
            response.send "application/json", '{"status": "Ok."}'
            
            if (data.type == 'confirmation_request') {
                def client = new RESTClient(data.confirm_url)
                def result = client.post() {
                    type 'application/json'
                    json token: data.token, url: "${BASE_URL}${request.uri}".replace('//', '/')
                }
            }

        }
        
    }
}

