import com.mulesoft.cloudhub.client.Application
import com.mulesoft.cloudhub.client.Connection
import com.mulesoft.cloudhub.client.DomainConnection

String username = System.getProperty('cloudhub.username')
String password = System.getProperty('cloudhub.password')

Connection cloudHub = new Connection(uri, username, password)
DomainConnection domainConnection = cloudHub.on(domain)

assert Application.Status.STARTED.equals(domainConnection.get().getStatus())

domainConnection.stop();
domainConnection.delete();