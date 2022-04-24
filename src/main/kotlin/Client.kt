import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Client {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = Client()

            while (true) {
//                val input = readln().split(" ").toTypedArray()

//                val parser: ArgumentParser = ArgumentParsers.newFor("Client").build()
//                    .defaultHelp(true)
//                    .description("Calculate checksum of given files.")
//                parser.addArgument("-t", "--type")
//                    .choices("GET", "POST", "OPTIONS").setDefault("OPTIONS")
//                    .help("Specify request method")
//
//
//                val ns: Namespace?
//                try {
//                    ns = parser.parseArgs(input)
//                } catch (e: ArgumentParserException) {
//                    parser.handleError(e)
//                    println("Unknown command")
//                    continue
//                }

                when ("OPTIONS") {
                    "GET" -> {
                        client.sendGetRequest()
                    }
                    "POST" -> {
                        client.sendPostRequest()
                    }
                    "OPTIONS" -> {
                        client.sendOptionRequest()
                    }
                }
            }
        }
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