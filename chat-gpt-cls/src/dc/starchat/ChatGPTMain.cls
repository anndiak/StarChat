Class dc.starchat.ChatGPTMain
{

ClassMethod GetAnswer(server As %String, port As %Integer, serviceName As %String, question As %String) As %String
{
    Set response = ##class(%Net.Remote.Gateway).%RemoteService(server, port, serviceName, question)
    Quit response
}

}
