import com.mulesoft.cloudhub.client.Connection
import com.mulesoft.cloudhub.client.DomainConnection

import static com.mulesoft.cloudhub.client.Application.Status.*

String username = System.getProperty('cloudhub.username')
String password = System.getProperty('cloudhub.password')

Connection cloudHub = new Connection(uri, username, password)
DomainConnection domainConnection = cloudHub.on(domain)

try
{
    boolean available = domainConnection.available()
    if (!available)
    {
        // App still exists
        checkStatus(domainConnection)
    }
    else
    {
        // App deleted
    }
}
catch (Exception e)
{
    // App still exists
    checkStatus(domainConnection)
}

private checkStatus(DomainConnection domainConnection)
{
    if (![UNDEPLOYING, UNDEPLOYED, DELETED].contains(domainConnection.get().getStatus()))
    {
        try
        {
            domainConnection.stop();
            domainConnection.delete();
        }
        catch (Exception e)
        {
            println("Problem undeploying application");
        }
        assert false, "Application was not undeployed"
    }
}


