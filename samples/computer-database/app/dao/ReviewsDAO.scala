package dao

import java.util.Date

import javax.inject.{ Inject, Singleton }
import models.{ Company, Computer, Page, Review }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.mvc.RequestHeader
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

@Singleton()
class ReviewsDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends CompaniesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  class Reviews(tag: Tag) extends Table[Review](tag, "REVIEW") {

    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def timestamp = column[Date]("TIMESTAMP")
    def score = column[Int]("SCORE")
    def comment = column[String]("COMMENT")
    def computerId = column[Long]("COMPUTER_ID")

    def * = (id.?, username, timestamp, score, comment, computerId) <> (Review.tupled, Review.unapply _)
  }

  private val reviews = TableQuery[Reviews]

  def reviewsForComputer(computer: Computer): Future[Seq[Review]] =
    computer.id match {
      case None => Future.successful(Seq())
      case Some(id) => reviewsForComputerId(id)
    }

  def reviewsForComputerId(id: Long): Future[Seq[Review]] =
    db.run(reviews.filter(_.computerId === id).result)

  def insert(review: Review): Future[Unit] =
    db.run(reviews += review).map(_ => ())

  def insert(reviews: Seq[Review]): Future[Unit] =
    db.run(this.reviews ++= reviews).map(_ => ())
}
