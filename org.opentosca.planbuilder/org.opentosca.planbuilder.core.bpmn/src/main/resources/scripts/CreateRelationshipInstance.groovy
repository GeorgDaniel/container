import groovy.json.*

println "======== Executing CreateRelationshipInstance.groovy with exec ID: ${execution.getId()} ========"

/*


"State">CREATED
"RelationshipTemplate">con_HostedOn_0<
"SourceURL">${MyTinyToDoDockerContainer_NodeInstanceURL}
"TargetURL">${DockerEngine_ NodeInstanceURL}

camunda:resultVariable="RelationshipTemplateID_RelationshipInstanceURL"

*/
def templateID = execution.getVariable("RelationshipTemplate");
def sourceUrlVar = execution.getVariable("SourceURL");
def sourceUrl = sourceUrlVar.substring(sourceUrlVar.lastIndexOf('/') + 1);
def targetUrlVar = execution.getVariable("TargetURL");
def targetUrl = targetUrlVar.substring(targetUrlVar.lastIndexOf('/') + 1);

println "RelationshipTemplateID: ${templateID}"
println "execution.getVariable(SourceURL): $sourceUrlVar"
println "sourceUrlVar.substring(sourceUrlVar.lastIndexOf('/') + 1): $sourceUrl"
println "execution.getVariable(TargetURL): $targetUrlVar"
println "targetUrlVar.substring(targetUrlVar.lastIndexOf('/') + 1): $targetUrl"

// create TemplateInstance URL from instance data API URL
def url = execution.getVariable("instanceDataAPIUrl").minus("instances");
url = url + "relationshiptemplates/" + templateID + "/instances"

println "url $url"

def post = new URL(url).openConnection();

// parse ServiceTemplateInstance ID and add it to the request body
def serviceInstanceURL = execution.getVariable("ServiceInstanceURL");
def serviceInstanceID = serviceInstanceURL.split("/")[serviceInstanceURL.split("/").length-1];
def message = '<api:CreateRelationshipTemplateInstanceRequest xmlns:api="http://opentosca.org/api" service-instance-id="' + serviceInstanceID + '" source-instance-id="' + sourceUrl + '" target-instance-id="' + targetUrl + '"/>';

println "serviceInstanceURL: $serviceInstanceURL"
println "serviceInstanceID: $serviceInstanceID"
println "message: $message"

// send Post to instance data API
post.setRequestMethod("POST");
post.setDoOutput(true);
post.setRequestProperty("Content-Type", "application/xml")
post.setRequestProperty("accept", "application/json")
post.getOutputStream().write(message.getBytes("UTF-8"));

def status = post.getResponseCode();
if (status == 200) {
    def resultText = post.getInputStream().getText();
    def slurper = new JsonSlurper();
    def json = slurper.parseText(resultText);
    def message2 = execution.getVariable("State");
    def url2 = json;

    // change the state
    if (message2 != null) {
        def put = new URL(url2 + "/state").openConnection();
        put.setRequestMethod("PUT");
        put.setDoOutput(true);
        put.setRequestProperty("Content-Type", "text/plain")
        put.getOutputStream().write(message2.getBytes("UTF-8"));

        def status2 = put.getResponseCode();
        if (status2 != 200) {
            execution.setVariable("ErrorDescription", "Received status code " + status2 + " while updating state of Instance with URL: " + url2);
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
        }
    }
    return json;
} else {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while creating Instance of RelationshipTemplate with ID: " + templateID);
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
}
