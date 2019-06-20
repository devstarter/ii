fun String?.nullOnBlank() = if (this?.isBlank() == true) null else this
