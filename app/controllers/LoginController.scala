package controllers

import java.math.BigInteger
import java.security.interfaces.RSAKey
import java.security.spec.RSAPublicKeySpec
import java.security.{ KeyFactory, PublicKey }
import java.util.Base64
import javax.inject.Inject

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import play.api.Configuration
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.OAuth2ClientService

import scala.concurrent.ExecutionContext

class LoginController @Inject() (oAuth2ClientService: OAuth2ClientService, configuration: Configuration, wsClient: WSClient, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.login(None))
  }

  def authorize = Action {
    Redirect(oAuth2ClientService.getAuthorizeUrl)
  }

  val getMessageListUrl = s"https://www.googleapis.com/gmail/v1/users/me/messages"
  def getMessageUrl(id: String) = s"https://www.googleapis.com/gmail/v1/users/userId/messages/$id"

  // Authorization: Bearer $ACCESS_TOKEN
  //
  // https://accounts.google.com/.well-known/openid-configuration
  // https://www.googleapis.com/oauth2/v3/certs
  def callback(code: String) = Action.async {
    oAuth2ClientService.getToken(code).flatMap { e =>
      val jwt = oAuth2ClientService.verifyIdToken(e.id_token)
      val userId = jwt.get.subject
      //          val nameClaim = jwt.getClaim("name")
      //          val name = nameClaim.asScala[String]
      //          val emailClaim = jwt.getClaim("email")
      //          val email = emailClaim.asScala[String]
      wsClient.url(getMessageListUrl).withHeaders("Authorization" -> s"Bearer ${e.access_token}").get.map { response =>
        val messages = response.json \\ "messages"
        messages.map{ message =>
          val id = (message \ "id").as[String]
          wsClient.url(getMessageUrl(id)).withHeaders("Authorization" -> s"Bearer ${e.access_token}").get.map{ messageResponse =>


          }

        }
        Ok(response.json)
      }
    }

  }

}
