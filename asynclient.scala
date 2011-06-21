import org.zeromq.ZMQ
import ZHelpers._

object asynclient  {
    //  ---------------------------------------------------------------------
    //  This is our client task
    //  It connects to the server, and then sends a request once per second
    //  It collects responses as they arrive, and it prints them out. We will
    //  run several client tasks in parallel, each with a different random ID.
    class ClientTask() extends Runnable {
        def run() {
            val ctx = ZMQ.context(1)
            val client = ctx.socket(ZMQ.XREQ)
            //  Generate printable identity for the client
            setID(client)
            val identity = new String(client getIdentity)
            client.connect("tcp://travis.in.escapemg.com:5570")
            val poller = ctx.poller(1)
            poller.register(client, ZMQ.Poller.POLLIN)
            var requestNbr = 0

            while (true) {
                // Tick once per 1/100th of a second pulling in arriving messages
                for (centitick <- 1 to 100) {
                    //poller.poll(10000)
                    poller.poll(100)
                    if (poller.pollin(0)) {
                        val msg = new ZMsg(client)
                        printf("%s : %s\n", identity, msg.bodyToString)
                    }
                }
                requestNbr += 1
                val msg = new ZMsg("request: %d" format requestNbr)
                client.sendMsg(msg)
            }
        }
    }

    //  This main thread simply starts several clients, and a server, and then
    //  waits for the server to finish.
    def main(args : Array[String]) {
        val Nclients = 3
        val clients = List.fill(Nclients)(new Thread(new ClientTask()))
        clients foreach (_.start)
    }
}
