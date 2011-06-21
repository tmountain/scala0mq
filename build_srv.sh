mkdir classes 2>/dev/null
scalac -d classes -classpath ./zmq.jar asyncsrv.scala  utils.scala ZMsg.scala Dns.scala
