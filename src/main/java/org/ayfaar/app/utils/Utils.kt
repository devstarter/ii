import com.google.gson.Gson

fun String?.nullOnBlank() = if (this?.isBlank() == true) null else this

inline fun <reified T> Gson.fromJson(json: String) : T = this.fromJson<T>(json, T::class.java)
inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)

