import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.OPTIONS
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface Api {

    @GET()
    fun getImg(
    ): Call<ImgResponse>

    @POST("/post")
    fun post(
        @Body stringResponse: StringResponse
    ): Call<StringResponse>

    @OPTIONS("/")
    fun option(
    ): Call<StringResponse>


    data class StringResponse(
        val response: String
    )

    data class ImgResponse(
        val response: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ImgResponse

            if (!response.contentEquals(other.response)) return false

            return true
        }

        override fun hashCode(): Int {
            return response.contentHashCode()
        }
    }

    companion object {

        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()


        fun invoke(): Api {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))

                .client(okHttpClient)
                .build()
                .create(Api::class.java)
        }
    }
}