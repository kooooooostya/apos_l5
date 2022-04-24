import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO


object ServerV2 {
    @JvmStatic
    fun main(args: Array<String>) {

        val port = 80
        val server = ServerSocket(port)
        println("Listening for connection on port $port ....")

        while (true) {
            val clientSocket = server.accept()
            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val outputStream = clientSocket.getOutputStream()
            val lines = arrayListOf<String>()

            var s: String
            var emptyLines = 0
            while (reader.readLine().also { s = it } != null) {
                println(s)
                lines.add(s)

                if (s.isEmpty()){
                    emptyLines++
                    if (emptyLines >= 2) {
                        break
                    }
                }
            }

            println("lines: $lines")
            if (lines.first().contains("GET")){
                println("get")
                handleGetRequest(outputStream)
            }else if(lines.first().contains("POST")){
                println("post")

                val contentLength = lines.find { it.startsWith("Content-Length:") }!!.split(" ").last().toInt()
                var stringResponse = ""
                lines.forEach{
                    stringResponse += it
                }
                val data = stringResponse.substring(stringResponse.length - contentLength)

                println("data: $data")
                handlePostRequest(data, outputStream)
            }else{
                println("else")
                handleOptionRequest(clientSocket, outputStream)
            }


            outputStream.close()
            reader.close()
            clientSocket.close()
        }


    }

    private fun handlePostRequest(data: String, out: OutputStream){
        out.write("HTTP/1.0 200 OK\r\n".toByteArray(Charset.defaultCharset()))
        out.write("Content-Type: text/html\r\n".toByteArray(Charset.defaultCharset()))
        out.write("Content-Length: ${data.length}\r\n".toByteArray(Charset.defaultCharset()))
        out.write("\r\n".toByteArray(Charset.defaultCharset()))
        out.write(data.toByteArray(Charset.defaultCharset()))
    }

    private fun handleGetRequest(out: OutputStream ){
        val encoded = Files.readAllBytes(Paths.get("C:\\Users\\Mi Book\\Desktop\\unic\\APOSZI\\aposzi_l5\\src\\files\\1.jpg"))

        val byteArrayOutputStream = ByteArrayOutputStream()
        val image = ImageIO.read(File("C:\\Users\\Mi Book\\Desktop\\unic\\APOSZI\\aposzi_l5\\src\\files\\1.jpg"))
        ImageIO.write(image, "jpg", byteArrayOutputStream)

        out.write("HTTP/1.0 200 OK\r\n".toByteArray(Charset.defaultCharset()))
        out.write("Content-Type: image/gif\r\n".toByteArray(Charset.defaultCharset()))
        out.write("Content-Length: ${encoded.size}\r\n".toByteArray(Charset.defaultCharset()))
        out.write("\r\n".toByteArray(Charset.defaultCharset()))

        out.write(byteArrayOutputStream.toByteArray())

    }

    private fun handleOptionRequest(socket: Socket, out: OutputStream){
        val response = "HTTP/1.0 204 No Content\n" +
                "Allow: GET,POST,OPTIONS\n" +
                "Local address: ${socket.localSocketAddress}\n"
        val responseBody = Gson().toJson(Api.StringResponse(response))


        out.write(responseBody.toByteArray())

    }
}