package services

import java.math.BigInteger
import java.security.spec.RSAPublicKeySpec
import java.security.{ KeyFactory, PublicKey }
import java.util.Base64
import javax.inject.{ Inject, Singleton }

import pdi.jwt.{ Jwt, JwtClaim, JwtJson }
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.{ Configuration, Logger }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

case class TokenResponse(access_token: String, token_type: String, expires_in: Int, refresh_token: Option[String], id_token: String)

@Singleton
class OAuth2ClientService @Inject() (configuration: Configuration, wsClient: WSClient)(implicit ec: ExecutionContext) {

  implicit val tokenResponseReads: Reads[TokenResponse] = Json.reads[TokenResponse]

  val authorizeEndpoint = "https://accounts.google.com/o/oauth2/v2/auth"
  val clientId = configuration.getString("clientId").get
  val clientSecret = configuration.getString("clientSecret").get
  val scope = configuration.getStringList("scope").mkString(" ")
  val redirectUri = "http://localhost:9000/callback"
  val params = Map(
    "response_type" -> "code",
    "client_id" -> clientId,
    "redirect_uri" -> redirectUri,
    "scope" -> "",
    "access_type" -> "offline",
    "approval_prompt" -> "force"
  )
  val url = s"$authorizeEndpoint?response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&access_type=offline&approval_prompt=force"

  val tokenEndpoint = "https://www.googleapis.com/oauth2/v4/token"

  private def getRSAPublicKey(n: String, e: String): PublicKey = {
    val kf = KeyFactory.getInstance("RSA")
    val nBytes = Base64.getUrlDecoder.decode(n)
    val eBytes = Base64.getUrlDecoder.decode(e)
    val bm = new BigInteger(1, nBytes)
    val be = new BigInteger(1, eBytes)
    val keySpec = new RSAPublicKeySpec(bm, be)
    kf.generatePublic(keySpec)
  }

  private lazy val rsaPublicKey = getRSAPublicKey(
    "6U2Oo9cENqBkEb5NJTN6TgYWpXAcvO29-VbSHtdg1JzJuqaCux4qyirV7GkIdt-crq75x8tO0iABIzhxCgoTwRMnbtKewLP2AxTg_aw60CUkm41HFN_firS52BXa-T6v7BFAmNSXduCQ5Ri3NdqudURpiiiH5QPTVSNETFOAsIjZagL1o5QmV6GO4oh3UlsZkRoLIrhaS4J2I-CYuz8e6Dq7cRe5AHU4ozU4tVFhipF6KclGytH0ilEb3G7EzgqL1n6bdNllEJ0emvrT-GqMmx-X6g4PK7_RnJY7upG0IAeYPOuew0obL1VR5RxybrD6QGm_PjxKdZ2vexP9Uc39Iw",
    "AQAB"
  )

  def getAuthorizeUrl: String = url

  def getToken(code: String): Future[TokenResponse] = {
    wsClient.url(tokenEndpoint).post(
      collection.immutable.Map(
        "code" -> Seq(code),
        "client_id" -> Seq(clientId),
        "client_secret" -> Seq(clientSecret),
        "redirect_uri" -> Seq(redirectUri),
        "access_type" -> Seq("offline"),
        "grant_type" -> Seq("authorization_code"),
        "scope" -> Seq.empty
      )
    ).map { response =>
        Logger.info(Json.prettyPrint(response.json))
        response.json.validate[TokenResponse].get
      }
  }

  def verifyIdToken(idToken: String): Try[JwtClaim] = {
    JwtJson.decode(idToken, rsaPublicKey)
    //    val verifier = JWT.require(Algorithm.RSA256(rsaPublicKey.asInstanceOf[RSAKey])).build()
    //    verifier.verify(idToken)
  }

}
