package controllers

import dao.{ ComputersDAO, ReviewsDAO, CompaniesDAO }
import javax.inject.Inject
import models.{ Company, Computer, Review }
import play.api.libs.json.{ JsObject, Json, Writes }
import play.api.mvc.{ AbstractController, ControllerComponents }

import scala.concurrent.{ ExecutionContext, Future }

class RESTApplication @Inject() (
    computersDao: ComputersDAO,
    reviewsDao: ReviewsDAO,
    companiesDao: CompaniesDAO,
    controllerComponents: ControllerComponents
)(implicit executionContext: ExecutionContext) extends AbstractController(controllerComponents) {

  implicit val companyWrites = new Writes[Company] {
    def writes(company: Company): JsObject = Json.obj(
      "id" -> company.id,
      "name" -> company.name
    )
  }

  implicit val computerWrites = new Writes[(Computer, Company, Int)] {
    def writes(computerAndCompany: (Computer, Company, Int)) = computerAndCompany match {
      case (computer, company, reviewCount) => Json.obj(
        "id" -> computer.id,
        "name" -> computer.name,
        "introduced" -> computer.introduced,
        "discontinued" -> computer.discontinued,
        "company" -> company,
        "reviewCount" -> reviewCount
      )
    }
  }

  implicit val reviewWrites = new Writes[Review] {
    def writes(review: Review): JsObject = Json.obj(
      "id" -> review.id,
      "username" -> review.username,
      "score" -> review.score,
      "comment" -> review.comment
    )
  }

  def listComputers() = Action.async { implicit request =>
    computersDao.list(pageSize = Int.MaxValue)
      .map(page => Ok(Json.toJson(page.items)))
  }

  // TODO: Add review count
  def getComputer(id: Long) = Action.async { implicit request =>
    val computerAndCompany = for {
      computer <- computersDao.findById(id)
      company <- computer.flatMap(_.companyId).map(companiesDao.getById).get
      reviews <- computer.map(reviewsDao.reviewsForComputer).get
    } yield (computer, company, reviews.length)

    computerAndCompany.map {
      case (Some(computer), Some(company), reviewCount) => Ok(Json.toJson((computer, company, reviewCount)))
      case (None, _, _) => NotFound
    }
  }

  def listReviewsForComputer(id: Long) = Action.async { implicit request =>
    val reviews = for {
      computer <- computersDao.findById(id)
      reviews <- computer match {
        case Some(c) => reviewsDao.reviewsForComputer(c)
        case None => Future.successful(Seq())
      }
    } yield reviews

    reviews.map(rs => Ok(Json.toJson(rs)))
  }
}

