package helpers

import javax.inject.Inject
import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      if(!requestHeader.uri.contains("assets")) {
        Logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned " +
          s"${result.header.status}")
      }
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}


