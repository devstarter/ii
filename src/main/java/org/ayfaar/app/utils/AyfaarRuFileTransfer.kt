package org.ayfaar.app.utils

import mu.KotlinLogging
import org.apache.commons.net.ftp.FTPClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

@Component
class AyfaarRuFileTransfer {
    private val ftpHost = "ftp.ayfaar.ru"
    @Value("\${ftp.ayfaar.ru.login}") private lateinit var ftpLogin: String
    @Value("\${ftp.ayfaar.ru.password}") private lateinit var ftpPassword: String

    constructor()
    internal constructor(ftpLogin: String, ftpPassword: String) {
        this.ftpLogin = ftpLogin
        this.ftpPassword = ftpPassword
    }

    fun upload(remoteFilename: String, data: String) {
        val file = File.createTempFile("ayfaar-ru-", ".data")
        file.writeText(data)

        val client = FTPClient()
//    client.addProtocolCommandListener(PrintCommandListener(System.out))

        try {
            client.connect(ftpHost)
            val loginSuccess = client.login(ftpLogin, ftpPassword)
            if (!loginSuccess) {
                KotlinLogging.logger {  }.error { "Login fail with login $ftpLogin and password $ftpPassword" }
            }
            client.enterLocalPassiveMode()
            val remoteFile = "/domains/ayfaar.ru/public_html/$remoteFilename"
            val storingSuccess = client.storeFile(remoteFile, file.inputStream())
            if (!storingSuccess) {
                KotlinLogging.logger {  }.error { "Error storing ftp file $remoteFile : ${client.replyString}" }
            }
            client.logout()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                client.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
