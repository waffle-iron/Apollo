#!/usr/bin/env groovy

@Grab(group = 'org.json', module = 'json', version = '20140107')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')

import groovyx.net.http.RESTClient
import org.json.JSONObject


String usageString = "add_organism.groovy <options>" +
        "Example: " +
        "./add_organism.groovy -name LargeBees -url http://localhost:8080/apollo/ -directory /opt/apollo/yeast -username ndunn@me.com -password supersecret"

def cli = new CliBuilder(usage:'add_organism.groovy <options>')
cli.setStopAtNonOption(true)
cli.url('URL to WebApollo instance',required:true,args: 1)
cli.name('organism common name',required: true,args: 1)
cli.directory('jbrowse data directory',required: true,args: 1)
cli.blatdb('blatdb directory',args: 1)
cli.genus('genus',args: 1)
cli.species('species',args: 1)
cli.username('username',required: true,args: 1)
cli.password('password',required:true,args: 1)
OptionAccessor options
try {
    options = cli.parse(args)

    if( !(options?.url && options?.name && options?.directory && options?.username && options?.password)){
        return
    }

} catch (e) {
    println(e)
    return
}

def argumentsArray = [
        name: options.name
        ,directory: options.directory
        ,username: options.username
        ,password: options.password
]
argumentsArray.blatdb = options.dblatdb ?: null
argumentsArray.genus = options.genus ?: null
argumentsArray.species = options.species ?: null

println "arguments array = ${argumentsArray}"

//body : [ firstName:'John', lastName:'Doe' ]
def client = new RESTClient(options.url)

def resp = client.post(
        contentType: 'text/javascript',
        path: '/apollo/organism/addOrganism'
, body : argumentsArray
)

assert resp.status == 200  // HTTP response code; 404 means not found, etc.
println resp.getData()