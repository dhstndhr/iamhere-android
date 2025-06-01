import android.content.SharedPreferences
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefs: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getString("access_token", null)

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            val bearerToken = "Bearer $token"
            requestBuilder.addHeader("Authorization", bearerToken)
            Log.d("인터셉터", "Authorization 헤더 추가됨: $bearerToken")
        } else {
            Log.d("인터셉터", "토큰 없음 - Authorization 헤더 추가되지 않음")
        }

        return chain.proceed(requestBuilder.build())
    }
}
