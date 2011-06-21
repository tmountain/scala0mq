mkdir classes 2>/dev/null
scalac -d classes -classpath ./zmq.jar asynclient.scala  utils.scala ZMsg.scala Dns.scala
