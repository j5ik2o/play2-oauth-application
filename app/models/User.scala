package models

import jp.t2v.lab.play2.pager.{ OrderType, Sortable }
import org.joda.time.DateTime
import scalikejdbc._
import skinny.orm.{ Alias, SkinnyCRUDMapper }

case class User(
  id: Option[Long] = None,
  name: String,
  email: String,
  accessToken: Option[String],
  tokenType: Option[String],
  expiresIn: Option[Int],
  refreshToken: Option[String],
  createAt: DateTime = DateTime.now(),
  updateAt: DateTime = DateTime.now()
)

object User extends SkinnyCRUDMapper[User] {

  override def tableName = "users"

  override def defaultAlias: Alias[User] = createAlias("u")

  def toNamedValues(record: User): Seq[(Symbol, Any)] = Seq(
    'name -> record.name,
    'email -> record.email,
    'accessToken -> record.accessToken,
    'tokenType -> record.tokenType,
    'expiresIn -> record.expiresIn,
    'refreshToken -> record.refreshToken,
    'createAt -> record.createAt,
    'updateAt -> record.updateAt
  )

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[User]): User =
    autoConstruct(rs, n)

  implicit object sortable extends Sortable[User] {
    override val default: (String, OrderType) = ("id", OrderType.Descending)
    override val defaultPageSize: Int = 10
    override val acceptableKeys: Set[String] = Set("id", "email", "createdAt", "updateAt")
  }
}
