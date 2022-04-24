import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket


fun main(args: Array<String>) {
    // création de la socket
    val port = 2001
    val serverSocket = ServerSocket(port)
    System.err.println("Serveur lancé sur le port : $port")

    // repeatedly wait for connections, and process
    while (true) {
        // on reste bloqué sur l'attente d'une demande client
        val clientSocket = serverSocket.accept()
        System.err.println("Nouveau client connecté")

        // on ouvre un flux de converation
        val `in` = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val out = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))

        // chaque fois qu'une donnée est lue sur le réseau on la renvoi sur
        // le flux d'écriture.
        // la donnée lue est donc retournée exactement au même client.
        var s: String
        while (`in`.readLine().also { s = it } != null) {
            println(s)
            if (s.isEmpty()) {
                break
            }
        }
        out.write("HTTP/1.0 200 OK\r\n")
        out.write("Content-Type: text/html\r\n")
        out.write("Content-Length: 59\r\n")
        out.write("\r\n")
        out.write("<TITLE>Exemple</TITLE>")
        out.write("<P>Ceci est une page d'exemple.</P>")

        // on ferme les flux.
        System.err.println("Connexion avec le client terminée")
        out.close()
        `in`.close()
        clientSocket.close()
    }
}