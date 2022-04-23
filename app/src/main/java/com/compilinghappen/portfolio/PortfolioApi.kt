package com.compilinghappen.portfolio

import android.util.Log
import com.compilinghappen.portfolio.auth.AuthStatus
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.LocalDate
import java.util.*

var UserToken: String? = null

data class TokenResponse(
    val token: String,
    val authStatus: AuthStatus
)

data class Tag(val id: Int, val name: String)

data class Album(
    val id: Int,
    val name: String,
    val description: String,
    val creationDate: Date,
    val likes: Int,
    val photos: List<String>,
    val tags: List<Tag>
)


object Repository {
    private val api by lazy {
        RetrofitApiAdapter()
    }

    suspend fun isTokenAlive(token: String?): Boolean {
        return token != null && api.isAlive(token)
    }

    suspend fun signUp(username: String, password: String): AuthStatus {
        return handleAuth(api.signUp(username, password))
    }

    suspend fun signIn(login: String, password: String): AuthStatus {
        return handleAuth(api.signIn(login, password))
    }

    suspend fun updateUserData(user: User) {
        api.updateUserData(UserToken!!, user)
    }

    private fun handleAuth(tokenResponse: TokenResponse): AuthStatus {
        if (tokenResponse.authStatus == AuthStatus.OK) {
            UserToken = "Bearer ${tokenResponse.token}"
            Log.d("TOKEN", UserToken!!)
        }
        return tokenResponse.authStatus
    }

    suspend fun getUserInfo(): User {
        return api.getUserInfo(UserToken!!)
    }

    suspend fun getPersonalAlbums(): List<Album> {
        return api.getPersonalAlbums(UserToken!!)
    }
}


private interface Api {
    suspend fun isAlive(token: String): Boolean
    suspend fun signUp(username: String, password: String): TokenResponse
    suspend fun signIn(username: String, password: String): TokenResponse
    suspend fun updateUserData(token: String, user: User)
    suspend fun getUserInfo(token: String): User
    suspend fun getPersonalAlbums(token: String): List<Album>
}

class MockApi : Api {
    override suspend fun isAlive(token: String): Boolean {
        return true
    }

    override suspend fun signUp(username: String, password: String): TokenResponse {
        return TokenResponse("TOKEN 12345", AuthStatus.OK)
    }

    override suspend fun signIn(username: String, password: String): TokenResponse {
        return TokenResponse("TOKEN 12345", AuthStatus.OK)
    }

    override suspend fun getUserInfo(token: String): User {
        return User(
            id = 1,
            name = "Дмитрий Морозов",
            about = "lorem ipsum dolor sit amet",
            birthDate = "24.12.2000",
            avatar = "https://i.pinimg.com/736x/14/83/20/14832097f59b84ce5454d6a2d273c51e--cute-cats-funny-cats.jpg"
        )
    }

    override suspend fun updateUserData(token: String, user: User) {

    }

    override suspend fun getPersonalAlbums(token: String): List<Album> {
        return listOf(
            Album(
                1,
                "Машинка",
                "3д модель машинки, сделанная в блендере и компасе 3д",
                Date(),
                95,
                listOf("https://sun9-82.userapi.com/s/v1/if2/1SQL9seIDxkEl6Sd1n-f7PCvYy15-m-7WPWTNZ5ifiCv-ZuA88xcvN4cEdqnt1XrX3NvO-okPB3Iutg68Hou2Olb.jpg?size=1280x720&quality=96&type=album"),
                listOf(Tag(1, "Blender"), Tag(2, "Компас3Д"))
            ),
            Album(
                2,
                "Колесо обозрения",
                "Ночные покатушки на колесе обозрения",
                Date(),
                103,
                listOf("https://sun1-98.userapi.com/s/v1/if1/0mABZNdFOwphmJpA951RrIKpzIs4_HT86o5csaTfGsYGnoZzvNo5dnND4LrxfgzwrtT2E0fc.jpg?size=604x586&quality=96&type=album"),
                listOf(Tag(3, "Тег"), Tag(4, "Ещё тег"))
            )
        )
    }
}

data class AuthenticationDto(val username: String, val passhash: String)

data class AuthAnswerDto(
    val access_token: String,
    val credential: CredentialsDto,
    val username: String
)

data class CredentialsDto(
    val id: Int,
    val passHash: String,
    val username: String
)

data class UserInfoDto(
    val id: Int?,
    val name: String,
    val description: String?,
    val profileImagePath: String?,
    val dateOfBirth: String?
)

data class UpdateUserInfoDto(
    val Name: String,
    val Description: String,
    val DateOfBirth: String,
    val Email: String = "example@email.com",
    val Phone: String = "ксяоми",
)

interface RetrofitPortfolioApi {
    @POST("/auth/signup")
    suspend fun signUp(@Body auth: AuthenticationDto): AuthAnswerDto

    @POST("/auth/login")
    suspend fun signIn(@Body auth: AuthenticationDto): AuthAnswerDto

    @GET("/users/getuserinfo")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfoDto

    @POST("/Users/Create")
    suspend fun updateUserInfo(@Header("Authorization") token: String, @Body info: UpdateUserInfoDto)
}


class RetrofitApiAdapter : Api {
    private val api: RetrofitPortfolioApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://u1661235.plsk.regruhosting.ru")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitPortfolioApi::class.java)
    }

    override suspend fun isAlive(token: String): Boolean {
        return true
    }

    override suspend fun signUp(username: String, password: String): TokenResponse {
        var token = ""
        var status = AuthStatus.UNKNOWN_ERROR

        try {
            val response = api.signUp(AuthenticationDto(username, password))
            token = response.access_token
            status = AuthStatus.OK
        }
        catch (ex: HttpException) {
            ex.printStackTrace()

            if (ex.code() == 404) {
                status = AuthStatus.INVALID_CREDENTIALS
            }
        }

        return TokenResponse(token, status)
    }

    override suspend fun signIn(username: String, password: String): TokenResponse {
        var token = ""
        var status = AuthStatus.UNKNOWN_ERROR

        try {
            val response = api.signIn(AuthenticationDto(username, password))
            token = response.access_token
            status = AuthStatus.OK
        }
        catch (ex: HttpException) {
            ex.printStackTrace()

            if (ex.code() == 401) {
                status = AuthStatus.INVALID_CREDENTIALS
            }
        }

        return TokenResponse(token, status)
    }

    override suspend fun updateUserData(token: String, user: User) {
        try {
            //val dto = UpdateUserInfoDto("Name", "Description", "24.12.2000", "email@email.com", "xiaomi")
            val dto = UpdateUserInfoDto(Name = user.name, Description = user.about, DateOfBirth = "24.12.2000")
            api.updateUserInfo(token, dto)
        }
        catch (ex: Exception) {}
    }

    override suspend fun getUserInfo(token: String): User {
        val dto = api.getUserInfo(token)
        return User(dto.id, dto.name, dto.description ?: "", dto.dateOfBirth ?: "24.12.2000", dto.profileImagePath)
    }

    override suspend fun getPersonalAlbums(token: String): List<Album> {
        return emptyList()
    }

}