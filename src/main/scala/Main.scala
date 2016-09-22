import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.DefaultKubernetesClient

object Main extends App {
  //val config = new ConfigBuilder().masterUrl("https://mymaster.com").build

  def getStatus(podName: String): String = {
    c.pods().inNamespace("default").withName(podName).get().getStatus.getPhase
  }

  val c = new DefaultKubernetesClient("https://192.168.99.100:8443")

  //println(c.pods().list())

  //val pod = new RunBuild

  val time = System.currentTimeMillis() / 1000

  val podName = s"test1-$time"
  val namespace = "default"

  println(s"Starting pod: $podName")

  val p: Pod = c
    .pods()
    .inNamespace(namespace)
    .createNew()
    .withNewMetadata()
    .withName(podName)
    .endMetadata()
    .withNewSpec()
    .withRestartPolicy("Never")
    .addNewContainer().withName(podName).withImage("perl").withCommand("sleep", "20")
    .endContainer().endSpec().done()

  println(p.getStatus)

  val succeeded = "Succeeded"
  var status = getStatus(podName)
  while(status != succeeded ) {
    println(s"status of pod $podName is not $succeeded, waiting")
    status = getStatus(podName)
    Thread.sleep(1000)
  }

  //println(c.pods().inNamespace("default").withName(podName).get().getStatus.getPhase)
  val deleteResult = c.pods().inNamespace("default").withName(podName).delete()
  println(s"Pod $podName successfuly deleted: $deleteResult")
}
