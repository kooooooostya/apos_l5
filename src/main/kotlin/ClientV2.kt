import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.Socket
import java.net.URL

class ClientV2 {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = ClientV2()

            while (true) {
                val input = readln().split(" ").toTypedArray()

                val parser: ArgumentParser = ArgumentParsers.newFor("Client").build()
                    .defaultHelp(true)
                    .description("Calculate checksum of given files.")
                parser.addArgument("-t", "--type")
                    .choices("GET", "POST", "OPTIONS").setDefault("OPTIONS")
                    .help("Specify request method")

                val ns: Namespace?
                try {
                    ns = parser.parseArgs(input)
                } catch (e: ArgumentParserException) {
                    parser.handleError(e)
                    println("Unknown command")
                    continue
                }

                when (ns.getString("type")) {
                    "GET" -> {
                        println("inter uri: ")
                        val uri = readln()
                        println(client.sendGetRequest(uri))
                    }
                    "POST" -> {
                        println("inter uri: ")
                        val uri = readln()

                        println("inter data str: ")
                        val data = readln()
                        println(client.sendPostRequest(uri, data))
                    }
                    "OPTIONS" -> {
                        println("inter uri: ")
                        val uri = readln()
                       println(client.sendOptionRequest(uri))
                    }
                }
            }
        }
    }

    val PORT = 80

    @Throws(Exception::class)
    fun sendGetRequest(url: String?): Map<String, String> {
        val responseMap: MutableMap<String, String> = HashMap()
        val urlObject = URL(url)
        println("Establishing Connection")
        val socket = Socket(InetAddress.getByName(urlObject.host), PORT)
        println("Connection Established")
        val printWriter = PrintWriter(socket.getOutputStream())
        printWriter.println("GET /" + urlObject.file + " HTTP/1.0")
        printWriter.println("Host: " + urlObject.host)
        printWriter.println("")
        printWriter.flush()
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        var line: String?
        var response = StringBuilder()
        var headerDone = false
        println("Fetching response. Please wait...")
        while (bufferedReader.readLine().also { line = it } != null) {
            response.append(
                """
                $line
                
                """.trimIndent()
            )
            if (line!!.isEmpty() && !headerDone) {
                responseMap["header"] = response.toString()
                headerDone = true
                response = StringBuilder()
            }
        }
        bufferedReader.close()
        printWriter.close()
        socket.close()
        println("Done!\n")
        responseMap["content"] = response.toString()
        return responseMap
    }

    @Throws(Exception::class)
    fun sendOptionRequest(_url: String?): String {
        val url = URL(_url)
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        http.requestMethod = "OPTIONS"
        http.setRequestProperty("Access-Control-Request-Method", "POST")
        http.setRequestProperty("Access-Control-Request-Headers", "content-type")

        val res = http.responseCode.toString() + " " + http.responseMessage
        http.disconnect()

        return res
    }


    @Throws(Exception::class)
    fun sendPostRequest(url: String?, data: String): Map<String, String>? {
        val responseMap: MutableMap<String, String> = HashMap()
        val urlObject = URL(url)
        println("Creating Connection")
        val socket = Socket(InetAddress.getByName(urlObject.host), PORT)
        println("Connection Established")
        val printWriter = PrintWriter(socket.getOutputStream())
        printWriter.println("POST /" + urlObject.file + " HTTP/1.0")
        printWriter.println("Host: " + urlObject.host)
        printWriter.println("Content-Length: " + data.length)
        printWriter.println() //Writing an empty line just to notify the server the header ends here
        // and next thing written will the data/content
        printWriter.println(data)
        printWriter.println()
        printWriter.flush()
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        var line: String?
        var response = StringBuilder()
        var headerDone = false
        println("Fetching response. Please wait...")
        while (bufferedReader.readLine().also { line = it } != null) {
            response.append(
                """
                $line
                
                """.trimIndent()
            )
            if (line?.isEmpty() == true && !headerDone) {
                headerDone = true
                response = StringBuilder()
            }
        }
        bufferedReader.close()
        printWriter.close()
        socket.close()
        println("Done!\n")
        responseMap["content"] = response.toString()
        return responseMap
    }

    fun sendPostRequest() {
        Api.invoke().post(Api.StringResponse("post response")).enqueue(object : Callback<Api.StringResponse> {
            override fun onResponse(
                call: Call<Api.StringResponse?>,
                response: Response<Api.StringResponse?>
            ) {
                println("Post отправлен: ${response.body()!!.response}")
                println(response)
            }

            override fun onFailure(call: Call<Api.StringResponse?>, t: Throwable) {
                println("failure ${t.message}")
            }
        })
    }

    fun sendGetRequest() {
        Api.invoke().getImg(
        ).enqueue(object : Callback<Api.ImgResponse> {
            override fun onResponse(
                call: Call<Api.ImgResponse>,
                response: Response<Api.ImgResponse>
            ) {
                println("Файл получен, размер: ${response.body()!!.response.size}")
                println(response)
            }

            override fun onFailure(call: Call<Api.ImgResponse>, t: Throwable) {
                println("failure ${t.message}")
            }
        })
    }

    fun sendOptionRequest() {
        Api.invoke().option().enqueue(object : Callback<Api.StringResponse> {
            override fun onResponse(
                call: Call<Api.StringResponse>,
                response: Response<Api.StringResponse>
            ) {
                println("Option: ${response.body()!!.response}")
                println(response)
            }

            override fun onFailure(call: Call<Api.StringResponse>, t: Throwable) {
                println("failure ${t.message}")
            }
        })
    }
}