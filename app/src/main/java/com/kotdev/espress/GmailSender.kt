package com.kotdev.espress
import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.concurrent.thread

object GmailSender {

    fun sendEmailWithAttachment(
        senderEmail: String,
        senderPassword: String,
        recipientEmail: String,
        subject: String,
        body: String,
        attachment: File,
        callback: (Boolean, String) -> Unit
    ) {
        thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    setSubject(subject)

                    val bodyPart = MimeBodyPart().apply {
                        setText(body)
                    }

                    val attachmentPart = MimeBodyPart().apply {
                        dataHandler = DataHandler(FileDataSource(attachment))
                        fileName = attachment.name
                    }

                    val multipart = MimeMultipart().apply {
                        addBodyPart(bodyPart)
                        addBodyPart(attachmentPart)
                    }

                    setContent(multipart)
                }

                Transport.send(message)
                callback(true, "Email sent successfully.")

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error: ${e.message}")
            }
        }
    }
}
