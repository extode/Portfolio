package com.compilinghappen.portfolio

import android.util.Log
import com.compilinghappen.portfolio.auth.AuthStatus
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

var UserToken: String? = null

data class TokenResponse(
    val token: String,
    val authStatus: AuthStatus
)

data class Tag(val id: Int, val name: String)

data class Photo(val id: Int, val path: String, val albumId: Int)

val PhotoPlaceholder = Photo(0, "https://vk.com/images/camera_200.png", 0)

data class Album(
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val creationDate: String,
    val likes: Int,
    val photos: List<Photo>,
    val tags: List<Tag>
) {
    val titlePhoto: Photo
        get() = if (photos.isNotEmpty()) photos[0] else PhotoPlaceholder
}


object Repository {
    private val api by lazy {
        RetrofitApiAdapter()
    }

    private var users: List<User>? = null

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
        }
        return tokenResponse.authStatus
    }

    suspend fun getUserInfo(): User {
        return api.getUserInfo(UserToken!!)
    }

    suspend fun getAlbums(userId: Int): List<Album> {
        return api.getAlbums(UserToken!!, userId)
    }

    suspend fun createAlbum(name: String, description: String, tags: List<String>): Boolean {
        return api.createAlbum(UserToken!!, name, description, tags)
    }

    suspend fun uploadImageToAlbum(file: File, albumId: Int): String {
        return api.uploadImageToAlbum(UserToken!!, file, albumId).value
    }

    suspend fun getTrending(): List<Album> {
        return api.getTrending(UserToken!!)
    }

    suspend fun getAllUsers(): List<User> {
        if (users == null) {
            users = api.getAllUsers(UserToken!!)
            users?.forEach {
                Log.d("USER_LOG", it.avatar ?: "NO AVATAR")
            }
        }
        return users!!
    }

    suspend fun likeAlbum(albumId: Int) {
        api.likeAlbum(UserToken!!, albumId)
    }

    suspend fun getUserById(id: Int): User? {
        return api.getUserById(UserToken!!, id)
    }

    suspend fun getAlbumById(id: Int): Album? {
        return api.getAlbumById(UserToken!!, id)
    }

    fun invalidateCache() {
        users = null
    }
}


private interface Api {
    suspend fun isAlive(token: String): Boolean
    suspend fun signUp(username: String, password: String): TokenResponse
    suspend fun signIn(username: String, password: String): TokenResponse
    suspend fun updateUserData(token: String, user: User)
    suspend fun getUserInfo(token: String): User

    suspend fun createAlbum(token: String, name: String, description: String, tags: List<String>): Boolean
    suspend fun getAlbums(token: String, userId: Int): List<Album>

    suspend fun uploadImageToAlbum(token: String, file: File, albumId: Int): FileUploadResponseDto

    suspend fun getTrending(token: String): List<Album>

    suspend fun getAllUsers(token: String): List<User>

    suspend fun likeAlbum(token: String, albumId: Int)

    suspend fun getUserById(token: String, userId: Int): User?
    suspend fun getAlbumById(token: String, albumId: Int): Album?
}

/*class MockApi : Api {
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

    override suspend fun getAlbums(token: String, userId: Int): List<Album> {
        return listOf(
            Album(
                1,
                "Машинка",
                "3д модель машинки, сделанная в блендере и компасе 3д",
                "",
                95,
                listOf(Photo(1, "https://sun9-82.userapi.com/s/v1/if2/1SQL9seIDxkEl6Sd1n-f7PCvYy15-m-7WPWTNZ5ifiCv-ZuA88xcvN4cEdqnt1XrX3NvO-okPB3Iutg68Hou2Olb.jpg?size=1280x720&quality=96&type=album", 1)),
                listOf(Tag(1, "Blender"), Tag(2, "Компас3Д"))
            ),
            Album(
                2,
                "Колесо обозрения",
                "Ночные покатушки на колесе обозрения",
                "",
                103,
                listOf(Photo(2, "https://sun1-98.userapi.com/s/v1/if1/0mABZNdFOwphmJpA951RrIKpzIs4_HT86o5csaTfGsYGnoZzvNo5dnND4LrxfgzwrtT2E0fc.jpg?size=604x586&quality=96&type=album", 2)),
                listOf(Tag(3, "Тег"), Tag(4, "Ещё тег"))
            )
        )
    }

    override suspend fun uploadImageToAlbum(token: String, file: File, albumId: Int): FileUploadResponseDto {
        TODO("Not yet implemented")
    }
}*/


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
    val profileImage: String?,
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

data class FileUploadResponseDto(
    val value: String,
)

data class CreateAlbumDto(
    val name: String,
    val description: String,
    val tags: List<String>
)

data class AlbumsWrapperDto(
    val albums: List<Album>
)

data class AlbumWrapperDto(
    val album: Album
)

interface RetrofitPortfolioApi {
    @POST("/auth/signup")
    suspend fun signUp(@Body auth: AuthenticationDto): AuthAnswerDto

    @POST("/auth/login")
    suspend fun signIn(@Body auth: AuthenticationDto): AuthAnswerDto

    @GET("/users/getuserinfo")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfoDto

    @POST("/Users/Create")
    suspend fun fillUserInfo(@Header("Authorization") token: String, @Body info: UpdateUserInfoDto)

    @GET("/Album/GetUserAlbums")
    suspend fun getAlbums(@Header("Authorization") token: String, @Query("userId") userId: Int): AlbumsWrapperDto

    @POST("/Album/AddImage")
    @Multipart
    suspend fun uploadAlbumImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part albumId: MultipartBody.Part
    ): FileUploadResponseDto

    @POST("/Album/Add")
    suspend fun createAlbum(
        @Header("Authorization") token: String,
        @Body data: CreateAlbumDto
    )

    @GET("/Album/MostLiked")
    suspend fun getTrending(@Header("Authorization") token: String): AlbumsWrapperDto

    @GET("/Users")
    suspend fun getAllUsers(@Header("Authorization") token: String): List<UserInfoDto>

    @POST("/Album/Liked")
    suspend fun likeAlbum(@Header("Authorization") token: String, @Query("albumId") albumId: Int)

    @GET("/Users/GetUser")
    suspend fun getUserById(@Header("Authorization") token: String, @Query("userId") userId: Int): UserInfoDto?

    @GET("/Album/GetAlbum")
    suspend fun getAlbumById(@Header("Authorization") token: String, @Query("albunId") albumId: Int): AlbumWrapperDto?
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
        } catch (ex: HttpException) {
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
        } catch (ex: HttpException) {
            ex.printStackTrace()

            if (ex.code() == 401) {
                status = AuthStatus.INVALID_CREDENTIALS
            }
        }

        return TokenResponse(token, status)
    }

    override suspend fun updateUserData(token: String, user: User) {
        try {
            val dto = UpdateUserInfoDto(
                Name = user.name,
                Description = user.about,
                DateOfBirth = "24.12.2000"
            )
            api.fillUserInfo(token, dto)
        } catch (ex: Exception) {
        }
    }

    private fun dto2user(dto: UserInfoDto): User {
        return User(
            dto.id,
            dto.name,
            dto.description ?: "",
            dto.dateOfBirth ?: "24.12.2000",
            dto.profileImage ?: dto.profileImagePath
        )
    }

    override suspend fun getUserInfo(token: String): User {
        val dto = api.getUserInfo(token)
        return dto2user(dto)
    }

    override suspend fun getAlbums(token: String, userId: Int): List<Album> {
        /*return listOf(
            Album(
                4, "Album", "Description", Date(), 104, listOf(
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                    "https://avatarko.ru/img/kartinka/1/Crazy_Frog.jpg",
                ),
                listOf(Tag(1, "Тег"), Tag(2, "Тег 2"))
            )
        )*/
        return api.getAlbums(token, userId).albums
    }

    override suspend fun uploadImageToAlbum(token: String, file: File, albumId: Int): FileUploadResponseDto {


        val requestBody = file.asRequestBody("*/*".toMediaType())
        val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val albumIdMB = MultipartBody.Part.createFormData("albumId", albumId.toString())

        return api.uploadAlbumImage(token, fileToUpload, albumIdMB)
    }

    override suspend fun getTrending(token: String): List<Album> {
        return api.getTrending(token).albums
    }

    override suspend fun getAllUsers(token: String): List<User> {
        return api.getAllUsers(token).map { dto2user(it) }
    }

    override suspend fun likeAlbum(token: String, albumId: Int) {
        api.likeAlbum(token, albumId)
    }

    override suspend fun createAlbum(token: String, name: String, description: String, tags: List<String>): Boolean {
        return try {
            api.createAlbum(token, CreateAlbumDto(name, description, tags))
            true
        } catch (ex: Exception) {
            false
        }
    }

    override suspend fun getAlbumById(token: String, albumId: Int): Album? {
        return api.getAlbumById(token, albumId)!!.album
    }

    override suspend fun getUserById(token: String, userId: Int): User? {
        return dto2user(api.getUserById(token, userId)!!)
    }
}