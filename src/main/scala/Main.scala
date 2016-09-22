import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.DefaultKubernetesClient

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  def getStatus(podName: String): String = {
    c.pods().inNamespace("default").withName(podName).get().getStatus.getPhase
  }

  def startPod(podName: String)  = {
    val random = scala.util.Random
    val randomSleep = random.nextInt(30)
    val sleepArgument = (5 + randomSleep).toString

    c
      .pods()
      .inNamespace(namespace)
      .createNew()
      .withNewMetadata()
      .withName(podName)
      .endMetadata()
      .withNewSpec()
      .withRestartPolicy("OnFailure")
      .addNewContainer().withName(podName).withImage("perl").withCommand("sleep", sleepArgument)
      .endContainer().endSpec().done()
  }

  def waitUntilCompleted(podName: String) = {
    val succeeded = "Succeeded"
    var status = getStatus(podName)
    while(status != succeeded ) {
      println(s"status of pod $podName is not $succeeded, waiting")
      status = getStatus(podName)
      Thread.sleep(1000)
    }
  }

  def waitUntilCompletedFuture(podName: String) = {
    Future{
      waitUntilCompleted(podName)
    }
  }

  def deletePod(podName: String) = {
    val deleteResult = c.pods().inNamespace("default").withName(podName).delete()
    println(s"Pod $podName successfully deleted: $deleteResult")
  }

  val c = new DefaultKubernetesClient("https://192.168.99.100:8443")

  val time = System.currentTimeMillis() / 1000

  val podName = s"test0-$time"
  val namespace = "default"

  val pods = List(s"test1-$time", s"test2-$time", s"test3-$time", s"test4-$time", s"test5-$time", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")

  println(s"Starting pods: $pods")

  val waitFutures = pods.map{ p =>
    startPod(p)
    waitUntilCompletedFuture(p)
  }

  Await.ready(Future.sequence(waitFutures), 30 minute)

  pods.foreach(deletePod)
}
