import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths


object Server {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val server = HttpServer.create(InetSocketAddress("localhost", 2121), 0)
        server.createContext("/", OptionHandler())
        server.createContext("/post", PostHandler())
        server.createContext("/get", GetHandler())
        server.createContext("/option", OptionHandler())
        server.executor = null // creates a default executor
        server.start()
    }


    internal class PostHandler : HttpHandler {
        @Throws(IOException::class)
        override fun handle(t: HttpExchange) {
            val requestStr = t.requestBody.readAllBytes().toString(Charset.defaultCharset())
            val response = "success: $requestStr"
            val responseBody = Gson().toJson(Api.StringResponse(response))
            t.sendResponseHeaders(200, responseBody.length.toLong())
            val os = t.responseBody
            os.write(responseBody.toByteArray())
            os.close()
        }
    }

    internal class OptionHandler : HttpHandler {
        @Throws(IOException::class)
        override fun handle(t: HttpExchange) {

            val response = "HTTP/1.1 200 OK\n" +
                    "Allow: GET,POST,OPTIONS\n" +
                    "Local address: ${t.localAddress}\n" +
                    "Remote address: ${t.remoteAddress}\n"
            val responseBody = Gson().toJson(Api.StringResponse(response))
            t.sendResponseHeaders(200, responseBody.length.toLong())
            val os = t.responseBody
            os.write(responseBody.toByteArray())
            os.close()
        }
    }

    internal class GetHandler : HttpHandler {
        @Throws(IOException::class)
        override fun handle(t: HttpExchange) {
            val h = t.responseHeaders
            h.add("Content-Type", "application/jpg")

            val encoded = Files.readAllBytes(Paths.get("C:\\Users\\Mi Book\\Desktop\\unic\\APOSZI\\aposzi_l5\\src\\files\\1.jpg"))

            val responseBody = Gson().toJson(Api.ImgResponse(encoded))
            t.sendResponseHeaders(200, responseBody.length.toLong())
            val os = t.responseBody
            os.write(responseBody.toByteArray(Charset.defaultCharset()))
            os.close()
        }
    }
}