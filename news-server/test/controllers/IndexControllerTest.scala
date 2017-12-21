package controllers

import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.mvc._
import play.api.test._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.controllers.IndexController;
import com.acadaca.fakenews.task._
import org.scalatest._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar

class IndexControllerTest extends PlaySpec with Results with MockitoSugar  {
  
  "IndexControler#index" should {
    val mockStartTask = mock[StartupTask]   
    
    val controller = new IndexController(mockStartTask)
    val result = controller.index().apply(FakeRequest())
    val bodyText = contentAsString(result)

    assert(status(result) == play.api.http.Status.OK)
    assert(bodyText.nonEmpty)
  }
  
}