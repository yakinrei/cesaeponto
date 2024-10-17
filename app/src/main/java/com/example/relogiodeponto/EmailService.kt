package com.example.relogiodeponto

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email


class EmailService(private val apiKey: String) {

    fun enviarEmail(destinatario: String, assunto: String, mensagem: String) {
        val from = Email("cesae@cesae.com")
        val to = Email(destinatario)
        val content = Content("text/plain", mensagem)
        val mail = Mail(from, assunto, to, content)

        val sg = SendGrid(apiKey)
        val request = Request()

        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()
            val response = sg.api(request)
            println("Status Code: " + response.statusCode)
            println("Body: " + response.body)
            println("Headers: " + response.headers)
        } catch (ex: Exception) {
            println("Erro ao enviar o e-mail: ${ex.message}")
        }
    }

    companion object {
        // Cria uma instância de EmailService usando uma chave de API fixa
        private val emailService = EmailService("")

        fun enviarEmail(destinatario: String, assunto: String, mensagem: String) {
            emailService.enviarEmail(destinatario, assunto, mensagem)
        }
    }

}





fun main() {
    val sendGridApiKey = ""  // Substitua pela sua chave de API
    val emailService = EmailService(sendGridApiKey)
    emailService.enviarEmail(
        "destinatario@example.com",
        "Assunto do E-mail",
        "Conteúdo do e-mail aqui"
    )
}
